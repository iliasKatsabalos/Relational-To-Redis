package textfiles;
import java.io.IOException;
import java.util.Scanner;
import redis.clients.jedis.Jedis;

//1.establish connection with Redis
//2.Scanner is created to write the file path
//3.we read the txt file using the ReadFile Class
//4. we mark the character ";" as the data pointer
//5. Looping through the data and the columns, we create hashsets
public class FileData {

	public static void main(String[ ] args) throws IOException {
			
		
			//1.establish connection with Redis
			Jedis jedis = new Jedis("localhost");
		
		
			//2.Scanner is created to write the file path
			Scanner sc=new Scanner(System.in);
			String file_name=sc.nextLine();
			try {
				
				//3.we read the txt file using the ReadFile Class
				ReadFile file = new ReadFile(file_name);
				String[] aryLines = file.OpenFile();
				//4. we mark the character ";" as the data pointer
				int dataPointer = java.util.Arrays.asList(aryLines).indexOf(";");
				int data = dataPointer + 1;
				
				
			
				//5. Looping through the data and the columns, we create hashsets. 
				for (int i=data; i<aryLines.length; i++){
					String[] row = aryLines[i].split(";");
					System.out.println(aryLines[0] + ":" + row[0]);
					
					for(int j=0; j<row.length; j++){
						System.out.println(aryLines[j+1] + " " + row[j]);
						jedis.hset(aryLines[0] + ":" + row[0],aryLines[j+1], row[j]);
					}
					
				}
						
						
						 
						
						//jedis.set(aryLines[0] + ":" + row[0]+ ":" + aryLines[j+1],row[j]);
						 //System.out.println("Stored string in redis:: "+ jedis.get(aryLines[0] + ":" + row[0]+ ":" + aryLines[j+1])); 
					
				
				 
				
			}
			
			catch (IOException e){
				System.out.println(e.getMessage());
			}
			
			
	}

}
