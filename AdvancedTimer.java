import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class AdvancedTimer {

    public static class Atividade {
        String nome;
        LocalTime inicio;
        LocalTime termino;
        boolean iaviso;
        boolean taviso;
    }

    public static void mostrarAviso(String titulo, String mensagem) {
        JDialog dialog = new JDialog();
        dialog.setTitle(titulo);
        dialog.setSize(320, 150);
        dialog.setLocationRelativeTo(null);
        dialog.setAlwaysOnTop(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        dialog.setLayout(new BorderLayout());

        JLabel label = new JLabel(mensagem, SwingConstants.CENTER);
        dialog.add(label, BorderLayout.CENTER);

        JButton botao = new JButton("OK");
        botao.setFont(new Font("Arial", Font.BOLD, 16));
        botao.setPreferredSize(new Dimension(100, 40));
        botao.addActionListener(e -> dialog.dispose());

        JPanel painelBotao = new JPanel();
        painelBotao.add(botao);

        dialog.add(painelBotao, BorderLayout.SOUTH);

        dialog.setVisible(true);

        javax.swing.Timer timer = new javax.swing.Timer(15000, e -> dialog.dispose());
        timer.setRepeats(false);
        timer.start();
    }

    public static void main(String[] args) throws InterruptedException {

        List<Atividade> atividades = new ArrayList<>();

        try {
            File arquivo = new File("C:\\Users\\Moraes\\Documents\\Advanced Alarm\\Alarmes.txt");
            Scanner leitor = new Scanner(arquivo, "UTF-8");

            while (leitor.hasNextLine()) {
                String linha = leitor.nextLine().trim();

                if (linha.isEmpty()) {
                    continue;
                }

                String[] dados = linha.split(",");

                if (dados.length != 3) {
                    System.out.println("Linha inválida: " + linha);
                    continue;
                }

                Atividade a = new Atividade();
                a.nome = dados[0].trim();
                a.inicio = LocalTime.parse(dados[1].trim());
                a.termino = LocalTime.parse(dados[2].trim());

                atividades.add(a);
            }

            leitor.close();

        } catch (Exception e) {
            System.out.println("Erro lendo o arquivo, verifica ai");
            e.printStackTrace();
            return;
        }

        long verificacoes = 0;

        while (true) {
            LocalTime horaAtual = LocalTime.now();
            long tempoFaltando = Long.MAX_VALUE;
            boolean temAtividadePendente = false;
            verificacoes++;

            if (verificacoes >= 50000) {
                System.out.println("Desligar rapazeada, deu por hoje");
                break;
            }

            for (Atividade a : atividades) {
                if (a.iaviso && a.taviso) {
                    continue;
                }

                temAtividadePendente = true;
                long tempoDaAtividade = 0;

                if (!a.iaviso) {
                    if (!horaAtual.isBefore(a.inicio)) {
                        mostrarAviso("Alarme: Inicio.", a.nome);
                        a.iaviso = true;
                        tempoDaAtividade = 1000;
                    } else {
                        tempoDaAtividade = Duration.between(horaAtual, a.inicio).toMillis();
                    }

                } else if (!a.taviso) {
                    if (!horaAtual.isBefore(a.termino)) {
                        mostrarAviso("Alarme: Termino.", a.nome);
                        a.taviso = true;
                        tempoDaAtividade = 1000;
                    } else {
                        tempoDaAtividade = Duration.between(horaAtual, a.termino).toMillis();
                    }
                }

                tempoFaltando = Math.min(tempoFaltando, tempoDaAtividade);
            }

            if (!temAtividadePendente) {
                System.out.println("Alarmes concluídos.");
                break;
            }

            tempoFaltando = Math.max(tempoFaltando, 1000);
            Thread.sleep(tempoFaltando);
        }
    }
}