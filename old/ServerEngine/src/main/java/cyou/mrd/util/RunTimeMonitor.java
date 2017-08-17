package cyou.mrd.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RunTimeMonitor implements Serializable{
	/**
	 * 
	 */
	private final static Logger log = LoggerFactory.getLogger(RunTimeMonitor.class);
	private static final long serialVersionUID = 1L;
	List<String> causeList = new ArrayList<String>();
	List<Long> tList = new ArrayList<Long>();

	public void knock(String cause) {
		long t = System.nanoTime();
		causeList.add(cause);
		tList.add(t);
	}

	public String toString(int opcode) {
		if (causeList.size() > 2) {
			long totalTime = (tList.get(tList.size() - 1).longValue() - tList.get(0).longValue()) / 1000000L;
			StringBuilder sb = new StringBuilder();
			sb.append("\r\n===========================\r\n");
			sb.append("RunTimeMonitor: ");
			sb.append("\r\n---------------------------\r\n");
			sb.append("NO.\tCAUSE\tTIME");
			sb.append("\r\n0\t").append(causeList.get(0));
			sb.append("\t0");
			for (int i = 1; i < causeList.size(); i++) {
				sb.append("\r\n").append(i);
				sb.append("\t").append(causeList.get(i));
				long t = (tList.get(i).longValue() - tList.get(i - 1).longValue()) / 1000000L;
				sb.append("\t").append(t).append("ms");
			}
			sb.append("\r\n---------------------------\r\n");
			sb.append("opcode : ").append(opcode).append(", total time : ").append(totalTime).append("ms");
			sb.append("\r\n===========================\r\n");
			return sb.toString();
		} else {
			return "knock size < 2";
		}
	}

	public static void main(String[] args) throws InterruptedException {
		RunTimeMonitor rt = new RunTimeMonitor();
		rt.knock("setp_1");
		Thread.sleep(100);
		rt.knock("setp_2");
		Thread.sleep(1002);
		rt.knock("setp_3");
		Thread.sleep(103);
		rt.knock("setp_4");
		Thread.sleep(1004);
		log.info(rt.toString());
	}
}
