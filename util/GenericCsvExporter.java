package util;
import java.lang.reflect.Field;
import java.util.List;

public class GenericCsvExporter {
    public static String export(List<?> data, List<String> fields) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.join(",", fields)).append("\n");

        for (Object obj : data) {
            for (int i = 0; i < fields.size(); i++) {
                try {
                    Field field = obj.getClass().getDeclaredField(fields.get(i));
                    field.setAccessible(true);
                    sb.append(field.get(obj));
                } catch (Exception e) {
                    sb.append("ERRO");
                }
                if (i < fields.size() - 1) sb.append(",");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}