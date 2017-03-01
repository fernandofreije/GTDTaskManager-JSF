package uo.sdi.business.impl.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import uo.sdi.business.Services;
import uo.sdi.business.exception.BusinessException;
import uo.sdi.dto.Task;

public class FreijeyPabloUtil {
	
	public static void orderAscending (List<Task> param){
		Collections.sort(param, new Comparator<Task>(){
			@Override
			public int compare(Task o1, Task o2) {
				if (o1.getPlanned()==null)
					return 1;
				if (o2.getPlanned()==null)
					return -1;
				return o1.getPlanned().compareTo(o2.getPlanned());
			}
		});
	}
	
	public static void orderDescending (List<Task> param){
		Collections.sort(param, new Comparator<Task>(){
			@Override
			public int compare(Task o1, Task o2) {
				if (o1.getPlanned()==null)
					return 1;
				if (o2.getPlanned()==null)
					return -1;
				return o2.getPlanned().compareTo(o1.getPlanned());
			}
		});
	}
	
	public static void groupByCategory (List<Task> param){
		Collections.sort(param, new Comparator<Task>(){
			@Override
			public int compare(Task o1, Task o2) {
				if (o1.getCategoryId()==null)
					return -1;
				if (o2.getCategoryId()==null)
					return 1;
				if (o1.getCategoryId()==o2.getCategoryId())
					return o1.getPlanned().compareTo(o2.getPlanned());
				
				return o2.getCategoryId().compareTo(o1.getCategoryId());
			}
		});
	}
	
	public static void groupByDay (List<Task> param){
		Collections.sort(param, new Comparator<Task>(){
			@Override
			public int compare(Task o1, Task o2) {
				if (o1.getPlanned()==null)
					return -1;
				if (o2.getPlanned()==null)
					return 1;
				if (o1.getPlanned().equals(o2.getPlanned())){
					if (o1.getCategoryId()==null)
						return -1;
					if (o2.getCategoryId()==null)
						return 1;
					return getCatName(o1).compareTo(getCatName(o2));
				}
					
				return o1.getPlanned().compareTo(o2.getPlanned());			
					
			}
		});
	}
	
	private static String getCatName(Task task){
		try {
			return Services.getTaskService().findCategoryById(task.getCategoryId()).getName();
		} catch (BusinessException e) {}
		return "";
	}

}
