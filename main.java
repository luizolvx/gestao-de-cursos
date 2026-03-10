import model.*;
import service.*;
import repository.InitialData;
import exception.EnrollmentException;
import util.GenericCsvExporter;
import java.util.*;
import java.util.stream.Collectors;

public class main {
    private static Scanner sc = new Scanner(System.in);
    private static User usuarioLogado = null;

    public static void main(String[] args) {
        InitialData.load();
        while (true) {
            if (usuarioLogado == null) {
                fazerLogin();
            } else {
                if (usuarioLogado instanceof Admin) menuAdmin();
                else menuAluno();
            }
        }
    }

    private static void fazerLogin() {
        System.out.println("\n--- ACADEMIA DEV LOGIN ---");
        System.out.print("E-mail: ");
        String email = sc.nextLine();
        usuarioLogado = InitialData.users.get(email);
        if (usuarioLogado == null) System.out.println("E-mail não cadastrado!");
    }

    private static void menuAdmin() {
        System.out.println("\n--- MENU ADMIN ---");
        System.out.println("1. Relatório: Instrutores Únicos (Set)");
        System.out.println("2. Relatório: Alunos por Plano (GroupingBy)");
        System.out.println("3. Atender Ticket (Queue FIFO)");
        System.out.println("4. Exportar Cursos CSV (Reflection)");
        System.out.println("5. Logout");
        System.out.print("Escolha: ");
        String op = sc.nextLine();

        switch (op) {
            case "1":
                Set<String> instrutores = InitialData.courses.values().stream()
                    .filter(c -> c.getStatus().equals("ACTIVE"))
                    .map(Course::getInstructorName)
                    .collect(Collectors.toSet());
                System.out.println("Instrutores Ativos: " + instrutores);
                break;
            case "2":
                Map<String, List<Student>> agrupados = InitialData.users.values().stream()
                    .filter(u -> u instanceof Student)
                    .map(u -> (Student) u)
                    .collect(Collectors.groupingBy(s -> s.getPlan().getPlanName()));
                agrupados.forEach((plano, alunos) -> System.out.println(plano + ": " + alunos.size() + " alunos"));
                break;
            case "3":
                SupportTicket t = InitialData.tickets.poll();
                System.out.println(t != null ? "Atendendo: " + t : "Fila vazia.");
                break;
            case "4":
                String csv = GenericCsvExporter.export(new ArrayList<>(InitialData.courses.values()), List.of("title", "instructorName"));
                System.out.println(csv);
                break;
            case "5": usuarioLogado = null; break;
        }
    }

    private static void menuAluno() {
    Student aluno = (Student) usuarioLogado;
    System.out.println("\n--- MENU ALUNO (" + aluno.getName() + ") ---");
    System.out.println("1. Matricular-se (Ver Catálogo)");
    System.out.println("2. Ver meu Progresso / Atualizar");
    System.out.println("3. Abrir Ticket de Suporte");
    System.out.println("4. Logout");
    System.out.print("Escolha: ");
    String op = sc.nextLine();

    switch (op) {
        case "1":
            // Exibe o catálogo antes de pedir o nome (Requisito 5)
            System.out.println("\n--- CURSOS DISPONÍVEIS ---");
            InitialData.courses.values().stream()
                .filter(c -> c.getStatus().equals("ACTIVE"))
                .forEach(c -> System.out.println("-> " + c.getTitle() + " [" + c.getDifficultyLevel() + "]"));
            
            System.out.print("\nDigite o nome do curso conforme escrito acima: ");
            String nome = sc.nextLine();
            Course c = InitialData.courses.get(nome);
            
            try {
                if (c == null || !c.getStatus().equals("ACTIVE")) {
                    throw new EnrollmentException("Curso não encontrado ou inativo. Digite exatamente como aparece na lista.");
                }
                // Verifica se já está matriculado
                boolean jaMatriculado = aluno.getEnrollments().stream()
                    .anyMatch(e -> e.getCourse().getTitle().equalsIgnoreCase(nome));
                
                if (jaMatriculado) throw new EnrollmentException("Você já está matriculado neste curso!");
                
                if (!aluno.getPlan().canEnroll(aluno.getEnrollments().size())) {
                    throw new EnrollmentException("Limite de matrículas do plano " + aluno.getPlan().getPlanName() + " atingido!");
                }
                
                aluno.getEnrollments().add(new Enrollment(aluno, c));
                System.out.println("Sucesso! Matrícula realizada em: " + c.getTitle());
            } catch (EnrollmentException e) {
                System.out.println("Atenção: " + e.getMessage());
            }
            break;

        case "2":
    if (aluno.getEnrollments().isEmpty()) {
        System.out.println("Você ainda não possui matrículas.");
    } else {
        System.out.println("\n--- MEU PROGRESSO ---");
        for (int i = 0; i < aluno.getEnrollments().size(); i++) {
            Enrollment e = aluno.getEnrollments().get(i);
            System.out.println(i + ". " + e.getCourse().getTitle() + " | Progresso: " + e.getProgress() + "%");
        }
        
        System.out.print("\nDeseja atualizar o progresso? (s/n): ");
        if (sc.nextLine().equalsIgnoreCase("s")) {
            try {
                System.out.print("Digite APENAS O NÚMERO do curso (ex: 0): ");
                int index = Integer.parseInt(sc.nextLine()); // Aqui dava o erro!
                
                if (index >= 0 && index < aluno.getEnrollments().size()) {
                    System.out.print("Novo progresso (0-100): ");
                    double novoProgresso = Double.parseDouble(sc.nextLine());
                    aluno.getEnrollments().get(index).setProgress(novoProgresso);
                    System.out.println("Progresso atualizado com sucesso!");
                } else {
                    System.out.println("Erro: Esse número de curso não existe na sua lista.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Erro: Você deve digitar um número válido, não o nome do curso!");
            }
        }
    }
    break;

        case "3":
            System.out.print("Assunto: "); String titulo = sc.nextLine();
            System.out.print("Mensagem: "); String msg = sc.nextLine();
            InitialData.tickets.add(new SupportTicket(titulo, msg, aluno.getEmail()));
            System.out.println("Ticket enviado para a fila FIFO!");
            break;
            
        case "4": 
            usuarioLogado = null; 
            System.out.println("Logout efetuado.");
            break;
    }
}
}