import service.BibliothequeService;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== APPLICATION DE GESTION DE BIBLIOTHÈQUE ===");
        System.out.println("Développé en Java avec PostgreSQL");
        System.out.println("=============================================\n");

        BibliothequeService bibliothequeService = new BibliothequeService();

        bibliothequeService.afficherMenu();

    }
}