import java.lang.ref.Cleaner;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TestCode {

    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
        map.put("test1", "test1");

        Set<String> strings = map.keySet();

        Cleaner cleaner = Cleaner.create();
        System.gc();

    }

}
