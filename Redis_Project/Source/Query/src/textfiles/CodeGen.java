package textfiles;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;


//import redis.clients.jedis.Jedis;


//1.create a temporary file
//2.This will output the full path where the file will be written to...
//4.Scanner is created to write the file path
//5.Read the query
//6.Assign all the lines of the query to an array
//6.1 We need to check if the query contains a where clause. Only then we call the parser method
//7.Inside the Parsers method, the sql where clause is parsed and transformed into a java condition
//8. We import package, libraries to the new executable file
//9. Create a connection with Jedis
//10. For each table we create a set of keys containing the appropriate records from redis
//10.1 All the sets have the format table[i] + "Set". for exampqle "StudentSet"
//11. For each table, we create equal amount of loops, that loop through the keys.
//11.1 The string that contains each key has the format tables[i] + "K" for example "StudentK"
//12. We need to check if we have a condition line in our txt files. If true, we print the condition
//13. We create a string called builf that contains the condition used in the if statement. 
//14. The method selectRes takes the SQL select statement and returns the java statement for printing eg jedis.hget(Student, "FName") + "," + jedis.hget...
//15. We close all the while loops that we have opened
//16. the SelectRes method has an sql select clause as an input, and returns the redis statement for example jedis.hget(....), jedis.hget.....
//17. the Parser method takes as input the sql where clause, and returns a string.
//17.1 the string returned is dynamic code that  includes the JedBool method which returns boolean
public class CodeGen {
	

	public static void main(String[ ] args)throws IOException{
			
			BufferedWriter writer = null;
	        try {
	            //1.create a temporary file
	            String joins = "Executable.java";
	            File logFile = new File(joins);

	            // 2.This will output the full path where the file will be written to...
	            writer = new BufferedWriter(new FileWriter(logFile));
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            try {
		
		
			//4.Scanner is created to write the file path
			Scanner sc=new Scanner(System.in);
			String file_name= sc.nextLine();
			
			//5.Read the query
			ReadFile file = new ReadFile(file_name);
				
			//6.Assign all the lines of the query to an array
			String[] aryLines = file.OpenFile();
			String select []= aryLines[0].replaceAll("\\s+","").split(",");
			String[] tables = aryLines[1].replaceAll("\\s+","").split(",");
			String build = new String();
			
			//6.1 We need to check if the query contains a where clause. Only then we call the parser method
			if (aryLines.length > 2){
				String condition[] = aryLines[2].split("(?=AND|OR)");
				
				//7. the Parser method returns a string containing the method JedBool(String Val1, String operator, String Val2)
				build = Parser(condition, tables);
			}	
				
			//8. We import package, libraries to the new executable file
			writer.write("import java.util.Iterator;import java.util.Set;import redis.clients.jedis.Jedis;");
			writer.newLine();
			writer.write("public class Executable {");
			writer.newLine();
			writer.write("public static void main(String[] args) {");
			writer.newLine();
				
			//9. Create a connection with Jedis
			writer.write("Jedis jedis = new Jedis(\"localhost\");");
			writer.newLine();
				
			//10. For each table we create a set of keys containing the appropriate records from redis
			//10.1 All the sets have the format table[i] + "Set". for exampqle "StudentSet"
			for(int i =0; i<tables.length; i++){
				//System.out.println("Set <String> " + tables[i] + "Set = jedis.keys(\""+tables[i]+ "*\");");
				writer.write("Set <String> " + tables[i] + "Set = jedis.keys(\""+tables[i]+ "*\");");
				writer.newLine();
			}
							
								
			//11. For each table, we create equal amount of loops, that loop through the keys.
			//11.1 The string that contains each key has the format tables[i] + "K" for example "StudentK"
			for (int i=0;i<tables.length;i++){
			
				//System.out.println("Iterator<String> k" + i +" = "+tables[i]+"Set"+".iterator();");
				//System.out.println("while (k"+i+ ".hasNext()){");
				//System.out.println("String "+tables[i]+"K"+ "= k"+i+".next();");
				writer.write("Iterator<String> k" + i +" = "+tables[i]+"Set"+".iterator();");
				writer.newLine();
				writer.write("while (k"+i+ ".hasNext()){");
				writer.newLine();
				writer.write("String "+tables[i]+"K"+ "= k"+i+".next();");
				writer.newLine();
			   
				}
			
			//12. We need to check if we have a condition line in our txt files. If true, we print the condition
			if (aryLines.length>2){
				//13. We create a string called builf that contains the condition used in the if statement. 
				writer.write("if("+build+"){"   );
				writer.newLine();
			}
			//14. The method selectRes takes the SQL select statement and returns the java statement for printing eg jedis.hget(Student, "FName") + "," + jedis.hget...
			writer.write(SelectRes(select)+"}");
			writer.newLine();
			
			//15. We close all the while loops that we have opened
			for (int i=0;i<tables.length;i++){
				System.out.println("}");
				writer.write("}");
				writer.newLine();
			}
			if (aryLines.length>2){
			writer.newLine();
			writer.write("}");
			writer.newLine();
			}
			
					
			
							
				
				
				
				
				
///////////////////////////////////////////////////Method JedBool creation///////////////////////////////////////////////////////////
//Jedbool Method has as input three parameters:
//String val1
//String operator
//String val2
// According to the type of the val1 and val2 the comparison is made for integer types or string types.
// We check if the value is a number or string, using the regex ("[0-9]+"). 
// After that, we compare the values, using the standard comparison if numeric, or the compareTo(String) method if string
// We return the boolean
			writer.write("public static  boolean JedBool(String val1, String oper, String val2){");
			writer.newLine();
			writer.write("boolean Res=false;");
			writer.newLine();	
			writer.write("if (val2.matches(\"[0-9]+\")){");
			writer.newLine();
			writer.write("if (val2.contains(\".\")|val1.contains(\".\")){");
			writer.newLine();
			writer.write("Double Num1 = Double.parseDouble(val1);");
			writer.newLine();
			writer.write("Double Num2 = Double.parseDouble(val2);");
			writer.newLine();
			
			writer.newLine();
			writer.write("if (oper.equals(\">\")){");
			writer.newLine();
			writer.write("Res = Num1 > Num2;");
			writer.newLine();
			writer.write("}");
			writer.newLine();
			writer.write("else if (oper.equals(\"<\")){");
			writer.newLine();
			writer.write("Res = Num1 < Num2;");
			writer.newLine();
			writer.write("}");
			writer.newLine();
			writer.write("else if (oper.equals(\"=\")){");
			writer.newLine();
			writer.write("Res = Num1.equals(Num2);");
			writer.newLine();
			writer.write("}");
			writer.newLine();
			writer.write("else if (oper.equals(\"!=\")){");
			writer.newLine();
			writer.write("Res = !Num1.equals(Num2);");
			writer.newLine();
			writer.write("}");
			writer.newLine();
			writer.write("}else{");
			writer.newLine();
			writer.write("Integer Num1 = Integer.parseInt(val1);");
			writer.newLine();
			writer.write("Integer Num2 = Integer.parseInt(val2);");
			writer.newLine();
				
			writer.newLine();
			writer.write("if (oper.equals(\">\")){");
			writer.newLine();
			writer.write("Res = Num1 > Num2;");
			writer.newLine();
			writer.write("}");
			writer.newLine();
			writer.write("else if (oper.equals(\"<\")){");
			writer.newLine();
			writer.write("Res = Num1 < Num2;");
			writer.newLine();
			writer.write("}");
			writer.newLine();
			writer.write("else if (oper.equals(\"=\")){");
			writer.newLine();
			writer.write("Res = Num1.equals(Num2);");
			writer.newLine();
			writer.write("}");
			writer.newLine();
			writer.write("else if (oper.equals(\"!=\")){");
			writer.newLine();
			writer.write("Res =!Num1.equals(Num2);");
			writer.newLine();
			writer.write("}");
			writer.newLine();
			writer.write("}");
			writer.newLine();
	
				
							
			writer.write("}");
			writer.newLine();	
				
						
			writer.write("else{");
			writer.newLine();
			writer.write("Integer Comp=val1.compareTo(val2);");
			writer.newLine();
			writer.write("if (oper.equals(\">\")){");
			writer.newLine();
			writer.write("if (Comp>0){");
			writer.newLine();
			writer.write("Res=true;");
			writer.newLine();
			writer.write("}else{");
			writer.newLine();
			writer.write("Res=false;");
			writer.newLine();
			writer.write("}");
			writer.newLine();
			writer.write("}");
			writer.newLine();
			writer.write("else if (oper.equals(\"<\")){");
			writer.newLine();
			writer.write("if (Comp<0){");
			writer.newLine();
			writer.write("Res=true;");
			writer.newLine();
			writer.write("}else{");
			writer.newLine();
			writer.write("Res=false;");
			writer.newLine();
			writer.write("}");
			writer.newLine();
			writer.write("}");
			writer.newLine();
			writer.write("else if (oper.equals(\"=\")){");
			writer.newLine();
			writer.write("if (Comp==0){");
			writer.newLine();
			writer.write("Res=true;");
			writer.newLine();
			writer.write("}else{");
			writer.newLine();
			writer.write("Res=false;");
			writer.newLine();
			writer.write("}");
			writer.newLine();
			writer.write("}");
			writer.newLine();
			writer.write("else if(oper.equals(\"!=\")){");
			writer.newLine();
			writer.write("if (Comp!=0){");
			writer.newLine();
			writer.write("Res=true;");
			writer.newLine();
			writer.write("}else{");
			writer.newLine();
			writer.write("Res=false;");
			writer.newLine();
			writer.write("}");
			writer.newLine();
			writer.write("}");
			writer.newLine();
			writer.write("}");
			writer.newLine();
			//System.out.println(Res);
			writer.write("return Res;");
/////////////////////////////////////////////////////End of JedBool/////////////////////////////////////////////////////////////////
			writer.newLine();
			writer.write("}");
			writer.newLine();
			writer.write("}");
				
				
				
				
				
				
				
				
			writer.close();
								  
		           } catch (Exception e) {
		            }
		    }
				
            
	}
	
	//16. the SelectRes method has an sql select clause as an input, and returns the redis statement for example jedis.hget(....), jedis.hget.....	
	public static String SelectRes(String[] select){
		for (int i=0;i<select.length;i++ ){
			select[i]=select[i].replace(".", "K,\"");
			select[i]=select[i]+"\"";
			select[i]="jedis.hget("+select[i]+")";
			
		}	
		String Fselect = new String();
		for (int i=0;i<select.length;i++){
			if (i+1<select.length){
			Fselect = Fselect + select[i]+" + \",\" + ";
			}else{
				Fselect = Fselect + select[i]+");";
			}
		}
		Fselect = "System.out.println(" + Fselect; 
		
		return Fselect;
		
	}
	
	//17. the Parser method takes as input the sql where clause, and returns a string.
	//17.1 the string returned is dynamic code that  includes the JedBool method which returns boolean
	public static String Parser (String[] condition, String[] tables){
		String pars = new String();
		for (int i =0; i<condition.length; i++){
			condition[i] = condition[i].replace("AND", "&").replace("OR","|").trim().replace(".", ",\"");
			for(int j =0 ; j<condition[i].length(); j++){
				
				if (condition[i].charAt(j)!='&' & condition[i].charAt(j)!='|' & condition[i].charAt(j)!=' ' & condition[i].charAt(j)!='('){
					condition[i] = condition[i].substring(0,j) + "JedBool(" + condition[i].substring(j, condition[i].length()) + ")";
					break;
				}
			}
			
			for(int j =0; j<tables.length; j++){
				condition[i] = condition[i].replace(tables[j]+",", "jedis.hget(" + tables[j] + "K,");	
			
			}
			condition[i] = condition[i].replace("=j", "\")=j");
			condition[i] = condition[i].replace(">j", "\")>j");
			condition[i] = condition[i].replace("<j", "\")<j");
			condition[i] = condition[i].replace("<\")>j", "\")<>j");
			
			condition[i] = condition[i].replace("=\"", "\")=\"");
			condition[i] = condition[i].replace(">\"", "\")>\"");
			condition[i] = condition[i].replace("<\"", "\")<\"");
			condition[i] = condition[i].replace("<\")>", "<>");
			condition[i] = condition[i].replace("<\")>", "<>");
			
			
			
			
			for(int j=condition[i].length()-1; j>0; j--){
				if (condition[i].charAt(j) == ')'){
					
					if (condition[i].charAt(j-1)!= '"' & condition[i].charAt(j-1)!=')' ){
						condition[i]=condition[i].substring(0, j)+"\"" + condition[i].substring(j, condition[i].length());
						break;
						
					}
				}
				
			}
			
					
			condition[i] = condition[i].replace("=", ",\"=\",");
			condition[i] = condition[i].replace("<>", ",\"!=\",");
			condition[i] = condition[i].replace(">", ",\">\",");
			condition[i] = condition[i].replace("<", ",\"<\",");
			
			
			Pattern pattern = Pattern.compile("jedis.hget");
	        Matcher  matcher = pattern.matcher(condition[i]);

	        int count = 0;
	        while (matcher.find())
	            count++;

	        if (count == 2){
	        	condition[i] = condition[i] + ")";
	        }
			
			
			//System.out.println(condition[i]);
			
			
			
		}
		
		
		
		///////////////////////////////final condition assembling//////////////////////////////////////////////////
		for (int i =0; i<condition.length; i++){
			pars = pars + condition[i];
		}
		
		return pars;
	}
	
	
	
	
	
	
	
}

