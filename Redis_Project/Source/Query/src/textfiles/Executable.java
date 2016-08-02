package textfiles;

import java.util.Iterator;import java.util.Set;import redis.clients.jedis.Jedis;
public class Executable {
public static void main(String[] args) {
Jedis jedis = new Jedis("localhost");
Set <String> StudentSet = jedis.keys("Student*");
Set <String> CountrySet = jedis.keys("Country*");
Set <String> GradeSet = jedis.keys("Grade*");
Iterator<String> k0 = StudentSet.iterator();
while (k0.hasNext()){
String StudentK= k0.next();
Iterator<String> k1 = CountrySet.iterator();
while (k1.hasNext()){
String CountryK= k1.next();
Iterator<String> k2 = GradeSet.iterator();
while (k2.hasNext()){
String GradeK= k2.next();
if(JedBool(jedis.hget(StudentK,"SSN"),"=",jedis.hget(GradeK,"SSN"))& JedBool(jedis.hget(StudentK,"ID_Country"),"=",jedis.hget(CountryK,"IDC"))& (JedBool(jedis.hget(CountryK,"CountryName"),"=","Greece")& (JedBool(jedis.hget(GradeK,"Mark"),">","7")| JedBool(jedis.hget(StudentK,"FName"),">","Mb")))){
System.out.println(jedis.hget(StudentK,"FName") + "," + jedis.hget(StudentK,"LName") + "," + jedis.hget(GradeK,"Mark") + "," + jedis.hget(CountryK,"CountryName"));}
}
}
}

}
public static  boolean JedBool(String val1, String oper, String val2){
boolean Res=false;
if (val2.matches("[0-9]+")){
if (val2.contains(".")|val1.contains(".")){
Double Num1 = Double.parseDouble(val1);
Double Num2 = Double.parseDouble(val2);

if (oper.equals(">")){
Res = Num1 > Num2;
}
else if (oper.equals("<")){
Res = Num1 < Num2;
}
else if (oper.equals("=")){
Res = Num1.equals(Num2);
}
else if (oper.equals("!=")){
Res = !Num1.equals(Num2);
}
}else{
Integer Num1 = Integer.parseInt(val1);
Integer Num2 = Integer.parseInt(val2);

if (oper.equals(">")){
Res = Num1 > Num2;
}
else if (oper.equals("<")){
Res = Num1 < Num2;
}
else if (oper.equals("=")){
Res = Num1.equals(Num2);
}
else if (oper.equals("!=")){
Res =!Num1.equals(Num2);
}
}
}
else{
Integer Comp=val1.compareTo(val2);
if (oper.equals(">")){
if (Comp>0){
Res=true;
}else{
Res=false;
}
}
else if (oper.equals("<")){
if (Comp<0){
Res=true;
}else{
Res=false;
}
}
else if (oper.equals("=")){
if (Comp==0){
Res=true;
}else{
Res=false;
}
}
else if(oper.equals("!=")){
if (Comp!=0){
Res=true;
}else{
Res=false;
}
}
}
return Res;
}
}