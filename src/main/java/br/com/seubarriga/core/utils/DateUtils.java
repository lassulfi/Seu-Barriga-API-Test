package br.com.seubarriga.core.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
	
	public static String getDataDiferencaDias(Integer dias) {
		
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, dias);
		
		return getDataFormatada(calendar.getTime());
	}
	
	public static String getDataFormatada(Date data) {
		DateFormat formatter = new SimpleDateFormat("dd/MM/YYYY");
		
		return formatter.format(data);
	}
	
	public static String getDataFormatoBanco(String data) {
		String[] vetor = data.split("/");
		
		return vetor[2] + "-" + vetor[1] + "-" + vetor[0] + "T03:00:00.000Z" ;
	}
}
