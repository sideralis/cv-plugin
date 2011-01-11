package Tests;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Map<String,String> sysEnvs = System.getenv();
		for(String s : sysEnvs.keySet()){
			System.out.println(s+" = "+sysEnvs.get(s) );
		}
		String command2;
		Runtime r = Runtime.getRuntime();
		command2 = "cmd.exe /c path";
		try {
			Process p2 = r.exec(command2);
			InputStream in = p2.getInputStream();
			BufferedReader inreader = new BufferedReader(new InputStreamReader (in));
			String line ;
			while((line = inreader.readLine())!=null){
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
