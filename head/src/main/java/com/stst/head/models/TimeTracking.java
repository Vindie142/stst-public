package com.stst.head.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.stst.head.repos.EmployeeRepo;
import com.stst.head.repos.TimeTrackingRepo;

@Entity
@Table(name="time_tracking")
public class TimeTracking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne (optional=false)
	@JoinColumn(name="employee_id")
	private Employee employee;
	@Column(nullable = false)
	private LocalDate dayDate;
	private LocalTime startTime;
	private LocalTime endTime;
	
	public static final int MAX_WORK_INTERVAL = 13; // the time in hours after which the end of the working day occurs
	public static final int CLOCK_WITH_AUTOMATIC_OUTPUT = 4; // the number of hours that are counted per day at the automatic end of the working day
	public static final int NUMBER_OF_DECIMAL_AFTER_ROUNDING_THE_HOURS = 1; // the number of decimal places after rounding the clock
		
	public TimeTracking() {}
	
	public TimeTracking(Employee employee) {
		this.employee = employee;
		this.dayDate = LocalDate.now();
		this.startTime = LocalTime.now();
	}
	public TimeTracking(Employee employee, LocalDate dayDate, LocalTime startTime) {
		this.employee = employee;
		this.dayDate = dayDate;
		this.startTime = startTime;
	}
	public TimeTracking(Employee employee, LocalDate dayDate, LocalTime startTime, LocalTime endTime) {
		this.employee = employee;
		this.dayDate = dayDate;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public static void validatingForCorrectness(Long employeeId, TimeTrackingRepo timeTrackingRepo, EmployeeRepo employeeRepo) {
		
		Optional<TimeTracking> oTime = timeTrackingRepo.find2( employeeId );
		if (oTime.isEmpty()) { // for the first time
			return;
		}
		TimeTracking time = oTime.get();
		if (!time.isOpen()) {
			return;
		}
		
		if (time.getDayDate().equals(LocalDate.now())) { // if it's the same day
			Long durationHours = Duration.between(time.getStartTime(), LocalTime.now()).getSeconds() / 3600;
			if (durationHours > MAX_WORK_INTERVAL) {
				time.setEndTime( time.getStartTime().plusHours(CLOCK_WITH_AUTOMATIC_OUTPUT) );
				timeTrackingRepo.save(time);
			}
		} else if (time.getDayDate().plusDays(1).equals(LocalDate.now())) { // if it's the next day
			Long durationHours = 24 - ( Duration.between(time.getStartTime(), LocalTime.now()).getSeconds() / 3600 );
			if (durationHours > MAX_WORK_INTERVAL) {
				if (time.getStartTime().plusHours(CLOCK_WITH_AUTOMATIC_OUTPUT).compareTo(time.getStartTime()) > 0) {
					time.setEndTime( time.getStartTime().plusHours(CLOCK_WITH_AUTOMATIC_OUTPUT) );
					timeTrackingRepo.save(time);
				} else {
					time.setEndTime(LocalTime.of(23,59));
					timeTrackingRepo.save(time);
					TimeTracking time2 = new TimeTracking(employeeRepo.findById(employeeId).get(), time.getDayDate().plusDays(1), LocalTime.of(0,0));
					time2.setEndTime( time.getStartTime().plusHours(CLOCK_WITH_AUTOMATIC_OUTPUT) );
					timeTrackingRepo.save(time2);
				}
			}
		} else { // if it's later then the next day
			if (time.getStartTime().plusHours(CLOCK_WITH_AUTOMATIC_OUTPUT).compareTo(time.getStartTime()) > 0) {
				time.setEndTime( time.getStartTime().plusHours(CLOCK_WITH_AUTOMATIC_OUTPUT) );
				timeTrackingRepo.save(time);
			} else {
				time.setEndTime(LocalTime.of(23,59));
				timeTrackingRepo.save(time);
				TimeTracking time2 = new TimeTracking(employeeRepo.findById(employeeId).get(), time.getDayDate().plusDays(1), LocalTime.of(0,0));
				time2.setEndTime( time.getStartTime().plusHours(CLOCK_WITH_AUTOMATIC_OUTPUT) );
				timeTrackingRepo.save(time2);
			}
		}
	}
	
	public static String getTimeOfTheWholeDay(Long employeeId, LocalDate dayDate, boolean onlyHours, TimeTrackingRepo timeTrackingRepo) { // returns hh:mm
		List<TimeTracking> times = timeTrackingRepo.findAll2(employeeId, dayDate);
		if (times.size() == 0) {
			return onlyHours ? "0" : "00:00:00";
		}
		
		int durationSecs = 0;
		for (TimeTracking t : times) {
			if (!t.isOpen()) {
				durationSecs += Duration.between( t.getStartTime(), t.getEndTime() ).getSeconds();
			} else {
				durationSecs += Duration.between( t.getStartTime(), LocalTime.now() ).getSeconds();
			}
		}
		
		if (onlyHours) {
			double durationSecsD = durationSecs;
			double durationHoursD = (durationSecsD / (60 * 60));
			BigDecimal durationHoursBD = new BigDecimal(durationHoursD);
			return durationHoursBD.setScale(NUMBER_OF_DECIMAL_AFTER_ROUNDING_THE_HOURS, RoundingMode.HALF_DOWN)+"";
		} else {
			int	hrsI = (durationSecs / (60 * 60)) % 24;
			int	minI = (durationSecs / 60) % 60;
			int secI = durationSecs % 60;
			String hrsS = hrsI > 9 ? ""+hrsI : "0" + hrsI;
			String minS = minI > 9 ? ""+minI : "0" + minI;
			String secS = secI > 9 ? ""+secI : "0" + secI;
			return hrsS + ":" + minS + ":" + secS;
		}
	}
	
	public void endTime(Long employeeId, TimeTrackingRepo timeTrackingRepo, EmployeeRepo employeeRepo) {
		validatingForCorrectness(employeeId, timeTrackingRepo, employeeRepo);
		
		if (LocalTime.now().compareTo(startTime) < 0) {
			setEndTime(startTime);
		}
		
		if (dayDate.equals(LocalDate.now())) {
			setEndTime(LocalTime.now());
		} else {
			setEndTime(LocalTime.of(23,59));
			timeTrackingRepo.save(this);
			TimeTracking time2 = new TimeTracking(employeeRepo.findById(employeeId).get(), dayDate.plusDays(1), LocalTime.of(0,0));
			time2.setEndTime( LocalTime.now() );
			timeTrackingRepo.save(time2);
		}
	}
	
	public String getIntervalTime(boolean onlyHours) {
		long durationSecs = 0;
		if (endTime == null) {
			durationSecs = Duration.between( startTime, LocalTime.now() ).getSeconds();
		} else {
			durationSecs = Duration.between( startTime, endTime ).getSeconds();
		}
		
		if (onlyHours) {
			double durationSecsD = durationSecs;
			double durationHoursD = (durationSecsD / (60 * 60));
			BigDecimal durationHoursBD = new BigDecimal(durationHoursD);
			return durationHoursBD.setScale(NUMBER_OF_DECIMAL_AFTER_ROUNDING_THE_HOURS, RoundingMode.HALF_DOWN)+"";
		} else {
			long hrsI = (durationSecs / (60 * 60)) % 24;
			long minI = (durationSecs / 60) % 60;
			long secI = durationSecs % 60;
			String hrsS = hrsI > 9 ? ""+hrsI : "0" + hrsI;
			String minS = minI > 9 ? ""+minI : "0" + minI;
			String secS = secI > 9 ? ""+secI : "0" + secI;
			return hrsS + ":" + minS + ":" + secS;
		}
	}
	
	public boolean isOpen() { // if it has startTime and does'n have endTime
		return endTime == null;
	}
	public LocalTime getCorrectDayTime() { // gives such a start time so that if there is a difference with the present time, you can see the number of hours worked
		return startTime; // исправить с учетом множества отрезков
	}
	
	public static class ToPrint {
		public String name = "";
		public List<String[]> hoursAndExtraHours = new ArrayList<String[]>();
		public BigDecimal allHours = new BigDecimal("0");
		public String allExtraHours = "";
		public ToPrint() {}
	}
	
	@Override
	public String toString() {
		String id = this.id == null ? "" : this.id.toString();
		String employee = this.employee == null ? "" : this.employee.getNormName();
		String dayDate = this.dayDate == null ? "" : this.dayDate.toString();
		String startTime = this.startTime == null ? "" : this.startTime.toString();
		String endTime = this.endTime == null ? "" : this.endTime.toString();
		return  "TimeTracking{"+
				"id="+id+", "+
				"employee="+employee+", "+
				"dayDate="+dayDate+", "+
				"startTime="+startTime+", "+
				"endTime="+endTime+"}";
	}
	
	@Override
    public boolean equals(Object obj) {
	    if (obj == this) {
	        return true;
	    }
	    if (obj == null || obj.getClass() != this.getClass()) {
	        return false;
	    }
	    TimeTracking mayBe = (TimeTracking) obj;
	    return mayBe.toString().equals(this.toString());
    }
	
	@Override
    public int hashCode() {
		int result = 31;
	    int a1 = id == null ? 1 : id.hashCode();
	    int a2 = employee == null ? 1 : employee.hashCode();
	    int a3 = dayDate == null ? 1 : dayDate.hashCode();
	    int a4 = startTime == null ? 1 : startTime.hashCode();
	    int a5 = endTime == null ? 1 : endTime.hashCode();
	    return result * (a1 + a2 + a3 + a4 + a5);
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public LocalDate getDayDate() {
		return dayDate;
	}

	public void setDayDate(LocalDate dayDate) {
		this.dayDate = dayDate;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public LocalTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}
}
