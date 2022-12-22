package com.stst.head;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.stst.head.services.DualStream;
import com.stst.head.services.Notifs;

@SpringBootApplication
public class HeadApplication {
	
	// \n [\s]*
	public static void main(String[] args) {
		PrintStream out = null;
		PrintStream err = null;
		try {
			out = new PrintStream(new FileOutputStream("logs/" + LocalDate.now().toString().replaceAll(":","_") + "_out.log"));
			PrintStream dual = new DualStream(System.out, out);
		    System.setOut(dual);
		    err = new PrintStream(new FileOutputStream("logs/" + LocalDate.now().toString().replaceAll(":","_") + "_err.log"));
		    dual= new DualStream(System.err, err);
		    System.setErr(dual);
		    
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Notifs.sendTgToAdminChat("Ошибка в работе с файлами для логирования!");
		} finally {
			SpringApplication.run(HeadApplication.class, args);
		    
		    out.close();
		    err.close();
		}
	}

}
