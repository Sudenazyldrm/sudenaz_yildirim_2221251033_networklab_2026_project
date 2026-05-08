/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sudenaz_yıldırım_2221251033_networklab_2026;
    import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author ASUS
 */
public class ChessServer {


    // Waiting player
    // Rakip bekleyen oyuncu
    private static Socket waitingPlayer = null;

    public static void main(String[] args) {

        int port = 5000;

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("[SERVER] Chess Server started on port " + port);
            System.out.println("[SERVER] Satranc server baslatildi");
            System.out.println();

            // Infinite loop
            // Sonsuz client kabul dongusu
            while (true) {

                System.out.println("[SERVER] Waiting for connection...");
                System.out.println("[SERVER] Baglanti bekleniyor...");

                Socket newPlayer = serverSocket.accept();

                System.out.println("[SERVER] New player connected: "
                        + newPlayer.getInetAddress());

                // No waiting player
                // Bekleyen oyuncu yoksa
                if (waitingPlayer == null) {

                    waitingPlayer = newPlayer;

                    System.out.println("[SERVER] Player waiting for opponent...");
                    System.out.println("[SERVER] Oyuncu rakip bekliyor...");

                    // Inform player
                    // Oyuncuya bilgi ver
                    newPlayer.getOutputStream()
                            .write("WAITING".getBytes());

                } else {

                    // Match players
                    // Oyunculari eslestir
                    Socket player1 = waitingPlayer;
                    Socket player2 = newPlayer;

                    waitingPlayer = null;

                    System.out.println("[SERVER] Match found!");
                    System.out.println("[SERVER] Eslesme bulundu!");

                    // Send colors
                    // Renkleri gonder
                    player1.getOutputStream()
                            .write("COLOR:WHITE".getBytes());

                    player2.getOutputStream()
                            .write("COLOR:BLACK".getBytes());

                    // Create handlers
                    // Client handlerlari olustur
                    ClientHandler handler1 =
                            new ClientHandler(player1, player2, "WHITE");

                    ClientHandler handler2 =
                            new ClientHandler(player2, player1, "BLACK");

                    handler1.start();
                    handler2.start();

                    System.out.println("[SERVER] Game started");
                    System.out.println("[SERVER] Oyun basladi");
                    System.out.println();
                }
            }

        } catch (Exception e) {

            System.out.println("[SERVER] Error / Hata: "
                    + e.getMessage());
        }
    }
}
    

