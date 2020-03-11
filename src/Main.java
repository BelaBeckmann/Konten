import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    private static List<Konto> konten = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        Konto ebk = new Konto("EBK", null, KontoTyp.BILANZKONTO);
        Konto sbk = new Konto("SBK", null, KontoTyp.BILANZKONTO);

        konten.add(ebk);
        konten.add(sbk);

        BufferedReader csvReader = new BufferedReader(new FileReader("Konten.csv"));

        String row = null;
        while ((row = csvReader.readLine()) != null) {
            String[] data = row.split(",");
            System.out.println(row);
        }

        String command = IOTools.readLine();
        while (!command.equalsIgnoreCase("exit")) {
            try {
                String[] commandWords = command.split(" ");
                switch (commandWords[0]) {
                    case "create":

                        Konto parentKonto = ebk;
                        if (commandWords.length == 4) {
                            Konto foundKonto = getKontoForName(commandWords[3]);
                            parentKonto = foundKonto == null ? parentKonto : foundKonto;
                        }

                        KontoTyp typ = KontoTyp.PASSIV;
                        if (commandWords.length == 3 && commandWords[2].equalsIgnoreCase("A")) {
                            typ = KontoTyp.AKTIV;
                        }

                        Konto newKonto = new Konto(commandWords[1], parentKonto, typ);
                        konten.add(newKonto);
                        System.out.println(newKonto.print());
                        break;
                    case "b":
                        Konto sollKonto = getKontoForName(commandWords[1]);
                        Konto habenKonto = getKontoForName(commandWords[2]);
                        if (sollKonto == null) {
                            System.out.println("Konto nicht gefunden: " + commandWords[1]);
                        }
                        if (habenKonto == null) {
                            System.out.println("Konto nicht gefunden: " + commandWords[2]);
                        }
                        if (sollKonto == null || habenKonto == null) {
                            break;
                        }

                        BigDecimal betrag = new BigDecimal(commandWords[3]);
                        Buchung buchung = new Buchung(sollKonto, habenKonto, betrag);
                        System.out.println(buchung.erstelleBuchungssatz());
                        System.out.println(sollKonto.print());
                        System.out.println(habenKonto.print());
                        break;
                    case "saldo":
                        for (Konto konto : ebk.getChildKontos()) {
                            konto.saldieren(sbk);
                        }
                    case "print":
                        for (Konto konto : konten) {
                            System.out.println(konto.print());
                        }
                        break;
                    case "del":
                        Konto foundKonto = getKontoForName(commandWords[1]);
                        if (foundKonto != null) {
                            konten.remove(foundKonto);
                            foundKonto.getParentKonto().getChildKontos().remove(foundKonto);
                            foundKonto.getChildKontos().forEach(konto -> konto.setParentKonto(null));
                            System.out.println("delete of " + commandWords[1] + " successfull");
                        }
                        break;
                    default:
                        System.out.println("unknown command!");
                        break;
                }
            }
            catch (Exception ex) {
                System.out.println("false parameter!");
                System.out.println(ex.getMessage());
            }
            command = IOTools.readLine();
        }
    }

    private static Konto getKontoForName(String name) {
        List<Konto> kontos = konten.stream().filter(konto -> konto.getName().equalsIgnoreCase(name)).collect(Collectors.toList());
        return kontos.isEmpty() ? null : kontos.get(0);
    }

}
