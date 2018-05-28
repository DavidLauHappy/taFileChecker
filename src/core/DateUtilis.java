package core;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtilis {
	
		public static boolean isWorkDay(String date,String skipDays){
			 if(!AppUtils.isNullOrEmpty(skipDays)){
				 String[] offDays=skipDays.split("\\|");
				 for(String day:offDays){
					 if("$weekend".equalsIgnoreCase(day)){
						  if(DateUtilis.isWeekEnd(date)){
							  return false;
						  }
					 }else{
						 if(day.equals(date)){
							 return false;
						 }
					 }
				 }
				 return true;
			 }else{
				 skipDays="$weekend";
				  if(DateUtilis.isWeekEnd(date)){
					  return false; 
				  }else{
					  return true;
				  }
			 }
		}
		
		
	public static String getNextWorkDate(String date,String offDays){
			DateFormat format = new SimpleDateFormat("yyyyMMdd");      
			Calendar   calendar=Calendar.getInstance();    
			Date dateObj=null;
			String nextDate=date;
			try {
				dateObj = format.parse(date);
				calendar.setTime(dateObj);
				calendar.add(Calendar.DAY_OF_YEAR, 1);
				 nextDate= format.format(calendar.getTime());
				while(!DateUtilis.isWorkDay(nextDate,offDays)){
					dateObj = format.parse(nextDate);
					calendar.setTime(dateObj);
					calendar.add(Calendar.DAY_OF_YEAR, 1);
					nextDate= format.format(calendar.getTime());
				}
			}catch (ParseException e) {
				e.printStackTrace();
			} 
			return nextDate;
		}
		
		private static boolean isWeekEnd(String date){
			DateFormat format = new SimpleDateFormat("yyyyMMdd");        
			Date dateObj=null;
			try {
				dateObj = format.parse(date);
				Calendar   calendar=Calendar.getInstance();    
				calendar.setTime(dateObj);
				int week=calendar.get(Calendar.DAY_OF_WEEK)-1;  
				if(week ==6 || week==0){
					return true;
				}
			  } 
			catch (ParseException e) {
				e.printStackTrace();
			} 
			return false;
		}
}
