package mgr;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Manager<T extends Manageable> {
	ArrayList<T> mList = new ArrayList<>();

	public void readAll(String filename, Factory fac) {
		Scanner filein = null;
		try {
			filein = new Scanner(new File(filename));
		} catch (FileNotFoundException e) {
			System.out.println("파일 열기 실패: " + filename);
			System.exit(0);
		}

		while (filein.hasNext()) {
			T t = (T)fac.create();
			t.read(filein);
			mList.add(t);
		}
		filein.close();
	}
	
	public ArrayList<T> getList() {
	    return mList;
	}
	
	public void printAll() {
		for (T t : mList)
			t.print();
		System.out.println();
	}
}
