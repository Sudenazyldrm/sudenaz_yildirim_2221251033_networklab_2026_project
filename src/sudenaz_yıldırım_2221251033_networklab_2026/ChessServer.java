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



    public static void main(String[] args) {

        // Define the port number the server will listen on
        // Sunucunun dinleyecegi port numarasini tanimla
        int port = 5000;

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("[SERVER] Chess Server started on port " + port);
            System.out.println("[SERVER] Satranc server " + port + " portunda baslatildi");
            System.out.println();

            System.out.println("[SERVER] Waiting for Player 1...");
            System.out.println("[SERVER] Oyuncu 1 bekleniyor...");

            Socket player1 = serverSocket.accept();

            System.out.println("[SERVER] Player 1 connected: " + player1.getInetAddress());
            System.out.println("[SERVER] Oyuncu 1 baglandi: " + player1.getInetAddress());
            System.out.println();

            System.out.println("[SERVER] Waiting for Player 2...");
            System.out.println("[SERVER] Oyuncu 2 bekleniyor...");

            Socket player2 = serverSocket.accept();

            System.out.println("[SERVER] Player 2 connected: " + player2.getInetAddress());
            System.out.println("[SERVER] Oyuncu 2 baglandi: " + player2.getInetAddress());
            System.out.println();

            // Send color information to clients
            // Clientlara renk bilgisini gonder
            player1.getOutputStream().write("COLOR:WHITE".getBytes());
            player2.getOutputStream().write("COLOR:BLACK".getBytes());

            System.out.println("[SERVER] Player 1 color: WHITE");
            System.out.println("[SERVER] Player 2 color: BLACK");
            System.out.println();

            // Create client handler threads
            // Clientlari dinleyen threadleri olustur
            ClientHandler handler1 = new ClientHandler(player1, player2, "WHITE");
            ClientHandler handler2 = new ClientHandler(player2, player1, "BLACK");

            handler1.start();
            handler2.start();

            System.out.println("[SERVER] Game started");
            System.out.println("[SERVER] Oyun basladi");

        } catch (Exception e) {
            System.out.println("[SERVER] Error / Hata: " + e.getMessage());
        }
    }
}
    

