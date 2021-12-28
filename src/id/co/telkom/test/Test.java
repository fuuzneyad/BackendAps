package id.co.telkom.test;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String[] digits ={"0&&5"};
		for(String s:digits)
			genereateLoop2(s);
	}
	
	@SuppressWarnings("unused")
	private static void genereateLoop(String s){
		Object digits = s;//"7&&-9",62850&&-2,25&&30,6285311&-3&&-9
		System.out.println("DIGITS: "+digits);
		if(digits!=null){
			String digs = digits.toString();
			String[] arr1 = digs.split("&");
			Integer firstDigits = toInt(arr1[0]);

			System.out.println("digit ke "+0+" ="+firstDigits);
			int loop=1;
			for(int i=0;i<arr1.length;i++){
				if(i!=0){
					if(arr1[i].equals("") && arr1.length>=i+1){
						String next = arr1[i+1];
						if(next.contains("-")){
							Integer lastLoop = toInt(firstDigits.toString().substring(0,firstDigits.toString().length()-1)+arr1[i+1].replace("-", ""));
							//System.out.println(firstDigits+" c s "+lastLoop);
							for(int x=firstDigits+1; x<=lastLoop; x++){
								System.out.println("digit ke "+loop+" ="+x);
								loop++;
								firstDigits=x;
							}
							
						}else{
							Integer lastLoop = toInt(arr1[i+1]);
							for(int x=firstDigits+1; x<=lastLoop; x++){
								System.out.println("digit ke "+loop+" ="+x);
								loop++;
								firstDigits=x;
							}
						}
						break;
					}else{
						arr1[i]=arr1[i].replace("-", "");
						System.out.println("digit ke "+loop+" ="+arr1[0].substring(0,arr1[0].length()-1)+arr1[i]);
						firstDigits=toInt(arr1[0].substring(0,arr1[0].length()-1)+arr1[i]);
						loop++;
					}
				
				}
			}
			
		}
	}
	
	private static void genereateLoop2(String s){
		Object digits = s;
		System.out.println("DIGITS: "+digits);
		if(digits!=null){
			String digs = digits.toString();
			String[] arr1 = digs.split("&");
			String firstDigits = arr1[0];

			System.out.println("digit ke "+0+" ="+firstDigits);
			int loop=1;
			for(int i=0;i<arr1.length;i++){
				if(i!=0){
					if(arr1[i].equals("") && arr1.length>=i+1){
						String next = arr1[i+1];
						if(next.contains("-")){
							Integer a = toInt(firstDigits.substring(firstDigits.length()-1));
							Integer b = toInt(next.trim().replace("-", ""));
							String c = firstDigits.substring(0,firstDigits.length()-1);
							for(Integer x=a+1;x<=b;x++){
								String dig = c+x;
								System.out.println("digit ke "+loop+" ="+dig);
								loop++;
								firstDigits=dig;
							}
							
						}else{
							Integer lastLoop = toInt(arr1[i+1]);
							for(Integer x=toInt(firstDigits)+1; x<=lastLoop; x++){
								if(toInt(firstDigits)==-1)
									break;
								System.out.println("digit ke "+loop+" ="+x);
								loop++;
								firstDigits=x.toString();
							}
						}
						break;
					}else{
						arr1[i]=arr1[i].replace("-", "");
						String a = arr1[0].substring(0,arr1[0].length()-1)+arr1[i];
						System.out.println("digit ke "+loop+" ="+a);
						firstDigits=a;
						loop++;
					}
				
				}
			}
			
		}
	}
	private static Integer toInt(String s){
		try{
			return Integer.parseInt(s);
		}catch(NumberFormatException e){
			return -1;
		}
	}
}
