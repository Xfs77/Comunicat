package models;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

public class Page {
	   
	   public List results;
	   public int pageSize;
	   public int page;
	   public boolean hasNext;
	   public boolean hasPrevious;
	   public int totalPages;
	   
	   public Page(List list, int page) {
	       
	       this.page = page;
	       this.pageSize =10;
	      
		   int registres=list.size();
		   if ((registres % pageSize)==0){
			   this.totalPages=registres/pageSize;
		   }
		   else{
			   this.totalPages=registres/pageSize+1;
		   }
	      
		   Integer a=list.size();
		   Integer b=(page-1) * pageSize;
		   Set hs=new HashSet();
		   Integer c= (page-1) * pageSize+pageSize+1;
		   hs.add(c);
		   hs.add(a);
		   Integer d=(Integer) Collections.min(hs);

		   results = list.subList((page-1) * pageSize, d);
	       this.hasNext=isNextPage();
	       this.hasPrevious=isPreviousPage();


	   }
	   
		   
	   
	   public boolean isNextPage() {
		   return results.size() > pageSize;
	   }
	   
	   public boolean isPreviousPage() {
	       return page > 0;
	   }
	   
	   public List getList() {
	       return isNextPage() ?
	           results.subList(0, pageSize) :
	           results;
	   }

	}