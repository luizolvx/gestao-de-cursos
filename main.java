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
        System.out.print("E-mail (admin@dev.com ou aluno@dev.com): ");
        String email = sc.nextLine();
        usuarioLogado = InitialData.users.get(email);
        if (usuarioLogado == null) System.out.println("E-mail não cadastrado!");
    }

    private static void menuAdmin() {
        System.out.println("\n--- MENU ADMINISTRADOR ---");
        System.out.println("1. Relatórios Analíticos (Stream API)");
        System.out.println("2. Atender Ticket de Suporte (FIFO)");
        System.out.println("3. Exportar Dados para CSV (Reflection)");
        System.out.println("4. Gerenciar Status de Curso (Ativar/Inativar)");
        System.out.println("5. Logout");
        System.out.print("Escolha: ");
        String op = sc.nextLine();

        switch (op) {
            case "1":
                exibirRelatorios();
                break;
            case "2":
                SupportTicket t = InitialData.tickets.poll();
                System.out.println(t != null ? "Processando: " + t : "Nenhum ticket na fila.");
                break;
            case "3":
                exportarDadosDinamico();
                break;
            case "4":
                gerenciarStatusCurso();
                break;
            case "5":
                usuarioLogado = null;
                break;
        }
    }

    private static void exibirRelatorios() {
        System.out.println("\n--- RELATÓRIOS DA PLATAFORMA ---");
        
        // 1. Instrutores únicos (Set + Stream)
        Set<String> instrutores = InitialData.courses.values().stream()
            .filter(c -> c.getStatus().equals("ACTIVE"))
            .map(Course::getInstructorName)
            .collect(Collectors.toSet());
        System.out.println("-> Instrutores Ativos: " + instrutores);

        // 2. Média Geral de Progresso
        double media = InitialData.users.values().stream()
            .filter(u -> u instanceof Student)
            .flatMap(u -> ((Student) u).getEnrollments().stream())
            .mapToDouble(Enrollment::getProgress)
            .average().orElse(0.0);
        System.out.printf("-> Média de Progresso Geral: %.2f%%\n", media);

        // 3. Aluno com mais matrículas (Optional)
        Optional<Student> topStudent = InitialData.users.values().stream()
            .filter(u -> u instanceof Student)
            .map(u -> (Student) u)
            .max(Comparator.comparingInt(s -> s.getEnrollments().size()));
        topStudent.ifPresent(s -> System.out.println("-> Aluno destaque: " + s.getName()));

        // 4. Cursos por Dificuldade (Ordenado)
        System.out.print("\nFiltrar cursos por nível (BEGINNER/INTERMEDIATE/ADVANCED): ");
        String level = sc.nextLine().toUpperCase();
        InitialData.courses.values().stream()
            .filter(c -> c.getDifficultyLevel().equals(level))
            .sorted(Comparator.comparing(Course::getTitle))
            .forEach(c -> System.out.println("   - " + c.getTitle()));
    }

    private static void exportarDadosDinamico() {
        System.out.println("\n--- EXPORTAÇÃO CSV ---");
        System.out.println("Escolha as colunas separadas por vírgula (ex: title,instructorName,status):");
        String colunasInput = sc.nextLine();
        List<String> colunas = Arrays.asList(colunasInput.split(","));
        
        String csv = GenericCsvExporter.export(new ArrayList<>(InitialData.courses.values()), colunas);
        System.out.println("\nResultado CSV:\n" + csv);
    }

    private static void gerenciarStatusCurso() {
        System.out.print("Digite o nome do curso para alterar status: ");
        String nome = sc.nextLine();
        Course c = InitialData.courses.get(nome);
        if (c != null) {
            c.setStatus(c.getStatus().equals("ACTIVE") ? "INACTIVE" : "ACTIVE");
            System.out.println("Novo status de " + c.getTitle() + ": " + c.getStatus());
        } else {
            System.out.println("Curso não encontrado.");
        }
    }

    private static void menuAluno() {
        Student aluno = (Student) usuarioLogado;
        System.out.println("\n--- MENU ALUNO (" + aluno.getName() + ") ---");
        System.out.println("1. Matricular-se (Ver Catálogo)");
        System.out.println("2. Ver meu Progresso / Atualizar");
        System.out.println("3. Abrir Ticket de Suporte");
        System.out.println("4. Cancelar Matrícula");
        System.out.println("5. Logout");
        System.out.print("Escolha: ");
        String op = sc.nextLine();

        switch (op) {
            case "1":
                listarEMatricular(aluno);
                break;
            case "2":
                verEAtualizarProgresso(aluno);
                break;
            case "3":
                abrirTicket(aluno);
                break;
            case "4":
                cancelarMatricula(aluno);
                break;
            case "5":
                usuarioLogado = null;
                break;
        }
    }

    private static void listarEMatricular(Student aluno) {
        System.out.println("\n--- CATÁLOGO DE CURSOS ATIVOS ---");
        InitialData.courses.values().stream()
            .filter(c -> c.getStatus().equals("ACTIVE"))
            .forEach(c -> System.out.println("-> " + c.getTitle() + " [" + c.getDifficultyLevel() + "]"));

        System.out.print("\nNome do curso: ");
        String nome = sc.nextLine();
        Course c = InitialData.courses.get(nome);

        try {
            if (c == null || !c.getStatus().equals("ACTIVE")) throw new EnrollmentException("Curso indisponível.");
            if (aluno.getEnrollments().stream().anyMatch(e -> e.getCourse().equals(c))) throw new EnrollmentException("Já matriculado.");
            if (!aluno.getPlan().canEnroll(aluno.getEnrollments().size())) throw new EnrollmentException("Limite do plano excedido.");

            aluno.getEnrollments().add(new Enrollment(aluno, c));
            System.out.println("Matrícula confirmada!");
        } catch (EnrollmentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void verEAtualizarProgresso(Student aluno) {
        if (aluno.getEnrollments().isEmpty()) {
            System.out.println("Nenhuma matrícula encontrada.");
            return;
        }
        for (int i = 0; i < aluno.getEnrollments().size(); i++) {
            Enrollment e = aluno.getEnrollments().get(i);
            System.out.println(i + ". " + e.getCourse().getTitle() + " | Progresso: " + e.getProgress() + "%");
        }
        System.out.print("Deseja atualizar? (s/n): ");
        if (sc.nextLine().equalsIgnoreCase("s")) {
            try {
                System.out.print("Índice do curso: ");
                int idx = Integer.parseInt(sc.nextLine());
                System.out.print("Novo progresso (0-100): ");
                double p = Double.parseDouble(sc.nextLine());
                aluno.getEnrollments().get(idx).setProgress(p);
                System.out.println("Atualizado!");
            } catch (Exception e) { System.out.println("Entrada inválida."); }
        }
    }

    private static void abrirTicket(Student aluno) {
        System.out.print("Título: "); String t = sc.nextLine();
        System.out.print("Mensagem: "); String m = sc.nextLine();
        InitialData.tickets.add(new SupportTicket(t, m, aluno.getEmail()));
        System.out.println("Enviado para fila FIFO.");
    }

    private static void cancelarMatricula(Student aluno) {
        System.out.print("Nome do curso para cancelar: ");
        String nome = sc.nextLine();
        boolean removido = aluno.getEnrollments().removeIf(e -> e.getCourse().getTitle().equalsIgnoreCase(nome));
        System.out.println(removido ? "Matrícula cancelada." : "Curso não encontrado nas suas matrículas.");
    }
}