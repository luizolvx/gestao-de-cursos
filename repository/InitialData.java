package repository;
import model.*;
import service.*;
import java.util.*;

public class InitialData {
    public static Map<String, Course> courses = new HashMap<>();
    public static Map<String, User> users = new HashMap<>();
    public static Queue<SupportTicket> tickets = new LinkedList<>();

    public static void load() {
        courses.put("Java OO", new Course("Java OO", "Dominando Objetos", "Gustavo Guanabara", 40, "BEGINNER"));
        courses.put("Spring Expert", new Course("Spring Expert", "API Profissional", "Nelio Alves", 80, "ADVANCED"));
        
        users.put("admin@dev.com", new Admin("Coordenador", "admin@dev.com"));
        users.put("aluno@dev.com", new Student("Aprendiz Dev", "aluno@dev.com", new BasicPlan()));
    }
}