/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package project_paa;

import java.awt.*;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.*;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;



public class MazeGenerator extends JPanel { 
    //private dibawah merupakan varibel
    private final int width;
    private final int height;
    private final int[][] maze;
    private final int cellSize;
    private int redX;
    private int redY;
    private int greenX;
    private int greenY;
    private Timer timer;
    private boolean redCanSeeGreen;
    private boolean isPaused = false;
    private JSlider greenDroidSightSlider;
    private JLabel sightLabel;
    // Deklarasikan variabel droid merah tambahan
    private int additionalRedX = -1;
    private int additionalRedY = -1;
    private int additionalRedDirection; // 0: atas, 1: kanan, 2: bawah, 3: kiri
    // Variabel tambahan untuk droid merah acak
    private Timer randomRedDroidTimer;
    private int randomRedX;
    private int randomRedY;
    private int additionalRedCount = 0;
    private boolean addRedDroid = false;
    private boolean gameEnded = false;

    public MazeGenerator(int width, int height, int cellSize) {
    this.width = width;
    this.height = height;
    this.maze = new int[height][width];
    this.cellSize = cellSize;
    setPreferredSize(new Dimension(1080, 900));

   timer = new Timer(400, new ActionListener() {
    public void actionPerformed(ActionEvent e) {
        boolean paused = false;
        if (!paused) { // Periksa apakah permainan sedang dijeda
            moveRedDroid();
            moveGreenDroid();
            MoveRedDroid();
            moveAdditionalRedDroid(); // Tambahkan pemanggilan metode ini
            checkVisibility();
            repaint();
        }
    }
        });
   
}

    public void generateMaze() {
       
        // Inisialisasi labirin dengan dinding di setiap sel
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                maze[i][j] = 1;
            }
        }

        // Posisi awal
        int startX = 1;
        int startY = 1;

        // Carilah jalur melalui algoritma DFS
        generatePath(startX, startY);

        // Tandai titik masuk dan keluar
        maze[startY][startX] = 2;
        maze[height - 2][width - 2] = 3;

        // Inisialisasi posisi droid merah dan hijau
        redX = startX;
        redY = startY;
        greenX = width - 2;
        greenY = height - 2;
        redCanSeeGreen = false;
    }
    
public void addRedDroid() {
    int maxRedCount = 6; // Jumlah maksimum droid merah tambahan yang diizinkan

    if (additionalRedCount >= maxRedCount) {
        JOptionPane.showMessageDialog(
                null,
                "Tidak dapat menambahkan lebih dari " + maxRedCount + " droid merah tambahan.",
                "Gagal Menambah Droid Merah",
                JOptionPane.ERROR_MESSAGE);
    } else {
        int newX = (int) (Math.random() * width);
        int newY = (int) (Math.random() * height);

        // Periksa apakah posisi baru valid dan tidak ada dinding atau droid di sana
        if (maze[newY][newX] == 0 && (newX != greenX || newY != greenY)) {
            additionalRedX = newX;
            additionalRedY = newY;

            // Tentukan arah pergerakan droid merah tambahan (mengarah ke droid hijau)
            if (additionalRedX < greenX) {
                additionalRedDirection = 1; // kanan
            } else if (additionalRedX > greenX) {
                additionalRedDirection = 3; // kiri
            } else if (additionalRedY < greenY) {
                additionalRedDirection = 2; // bawah
            } else {
                additionalRedDirection = 0; // atas
            }

            // Tampilkan notifikasi jumlah droid merah yang telah ditambahkan
            JOptionPane.showMessageDialog(
                    null,
                    "Droid merah berhasil ditambahkan.",
                    "Tambah Droid Merah",
                    JOptionPane.INFORMATION_MESSAGE);

            additionalRedCount++; // Tambahkan jumlah droid merah tambahan yang ditambahkan
        } else {
            // Tampilkan notifikasi jika posisi baru tidak valid
            JOptionPane.showMessageDialog(
                    null,
                    "Posisi baru tidak valid. Coba lagi.",
                    "Gagal Menambah Droid Merah",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}


    public void addGreenDroid() {
        int newX = (int) (Math.random() * width);
        int newY = (int) (Math.random() * height);

        // Periksa apakah posisi baru valid dan tidak ada dinding atau droid di sana
        if (maze[newY][newX] == 0 && (newX != redX || newY != redY)) {
            greenX = newX;
            greenY = newY;
        }
    }
    
    public void MoveRedDroid() {
        int newX = (int) (Math.random() * width);
        int newY = (int) (Math.random() * height);

        // Periksa apakah posisi baru valid dan tidak ada dinding atau droid di sana
        if (maze[newY][newX] == 0 && (newX != greenX || newY != greenY)) {
            redX = newX;
            redY = newY;
        }
    }
    
   private void moveAdditionalRedDroid() {
    int newX = additionalRedX;
    int newY = additionalRedY;

    // Ubah posisi droid merah tambahan berdasarkan arah pergerakan
    if (additionalRedDirection == 0) {
        newY--;
    } else if (additionalRedDirection == 1) {
        newX++;
    } else if (additionalRedDirection == 2) {
        newY++;
    } else if (additionalRedDirection == 3) {
        newX--;
    }

    // Periksa apakah posisi baru valid dan tidak ada dinding atau droid di sana
    if (newX >= 0 && newX < width && newY >= 0 && newY < height && maze[newY][newX] == 0) {
        additionalRedX = newX;
        additionalRedY = newY;
    } else {
        // Jika posisi baru tidak valid, ubah arah pergerakan droid merah tambahan secara acak
        additionalRedDirection = (int) (Math.random() * 4);
    }
    
    // Memeriksa jika droid merah tambahan bertemu dengan droid hijau
if (additionalRedX == greenX && additionalRedY == greenY) {
    gameOver();
}

}
   private void gameOver() {
    JOptionPane.showMessageDialog(
            null,
            "Game Over!",
            "Game Over",
            JOptionPane.INFORMATION_MESSAGE);
    // Kode tambahan untuk menghentikan permainan
    }

    private void generatePath(int x, int y) {
        // Atur titik saat ini sebagai jalur yang valid
        maze[y][x] = 0;

        // Daftar arah acak
        int[][] directions = new int[][] {
            {1, 0},  // Timur
            {0, 1},  // Selatan
            {-1, 0}, // Barat
            {0, -1}  // Utara
        };

        // Acak urutan arah
        shuffleArray(directions);

        for (int[] direction : directions) {
            int dx = direction[0];
            int dy = direction[1];

            int nextX = x + (dx * 2);
            int nextY = y + (dy * 2);

            // Periksa apakah titik berikutnya dalam batas labirin
            if (nextX > 0 && nextX < width && nextY > 0 && nextY < height && maze[nextY][nextX] != 0) {
                // Hancurkan dinding di antara titik saat ini dan titik berikutnya
                maze[y + dy][x + dx] = 0;

                // Lakukan rekursif untuk titik berikutnya
                generatePath(nextX, nextY);
            }
        }
    }

    private void shuffleArray(int[][] array) {
        int index;
        int[] temp;
        for (int i = array.length - 1; i > 0; i--) {
            index = (int) (Math.random() * (i + 1));
            temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }
    
    private void moveRedDroid() {
    if (!isPaused) {
        int direction = (int) (Math.random() * 4);

        switch (direction) {
            case 0: // Kanan
                if (redX + 1 < width && maze[redY][redX + 1] == 0) {
                    redX++;
                }
                break;
            case 1: // Kiri
                if (redX - 1 >= 0 && maze[redY][redX - 1] == 0) {
                    redX--;
                }
                break;
            case 2: // Atas
                if (redY - 1 >= 0 && maze[redY - 1][redX] == 0) {
                    redY--;
                }
                break;
            case 3: // Bawah
                if (redY + 1 < height && maze[redY + 1][redX] == 0) {
                    redY++;
                }
                break;
        }

        // Memeriksa jika droid merah utama bertemu dengan droid hijau
        if (redX == greenX && redY == greenY) {
             JOptionPane.showMessageDialog(null, "Game Over!", "Permainan Berakhir", JOptionPane.INFORMATION_MESSAGE);
            gameOver();
}

    }
}

private void moveGreenDroid() {
    if (!isPaused) {
        int[][] directions = new int[][]{
            {1, 0},  // Timur
            {0, 1},  // Selatan
            {-1, 0}, // Barat
            {0, -1}  // Utara
        };

        shuffleArray(directions);

        for (int[] direction : directions) {
            int dx = direction[0];
            int dy = direction[1];

            int nextX = greenX + dx;
            int nextY = greenY + dy;

            if (nextX >= 0 && nextX < width && nextY >= 0 && nextY < height && maze[nextY][nextX] == 0
                    && (nextX != redX || nextY != redY)) {
                greenX = nextX;
                greenY = nextY;
                break;
            }
        }
    }
}

    private void checkVisibility() {
        int distance = Math.abs(redX - greenX) + Math.abs(redY - greenY);

        if (distance <= 2) {
            redCanSeeGreen = true;
        } else {
            redCanSeeGreen = false;
        }
    }
    

    @Override
public void paintComponent(Graphics g) {
    super.paintComponent(g);

    // Gambar labirin
    for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
            switch (maze[i][j]) {
                case 0: // Jalur
                    g.setColor(Color.WHITE);
                    break;
                case 1: // Dinding
                    g.setColor(Color.BLACK);
                    break;
                case 2: // Titik masuk
                    g.setColor(Color.GREEN);
                    break;
                case 3: // Titik keluar
                    g.setColor(Color.RED);
                    break;
            }
            g.fillRect(j * cellSize, i * cellSize, cellSize, cellSize);
        }
    }

    // Gambar droid hijau
    g.setColor(Color.GREEN);
    g.fillOval(greenX * cellSize, greenY * cellSize, cellSize, cellSize);

    // Gambar droid merah
    g.setColor(Color.RED);
    g.fillOval(redX * cellSize, redY * cellSize, cellSize, cellSize);

    // Gambar droid merah tambahan 
    if (additionalRedX >= 0 && additionalRedY >= 0) {
        g.setColor(Color.RED);
        g.fillOval(additionalRedX * cellSize, additionalRedY * cellSize, cellSize, cellSize);
    }

    // Gerakkan droid merah tambahan
    moveAdditionalRedDroid();

    // Gambar sudut pandang droid merah
    if (redCanSeeGreen) {
        g.setColor(new Color(255, 0, 0, 128)); // Warna merah dengan transparansi
        g.fillRect(redX * cellSize, redY * cellSize, cellSize, cellSize);
    }
}
    
    public static void main(String[] args) {
    int width = 21;
    int height = 21;
    int cellSize = 30;

    MazeGenerator mazeGenerator = new MazeGenerator(width, height, cellSize);
    mazeGenerator.generateMaze();

    JFrame frame = new JFrame("Maze");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);
    frame.setLayout(new BorderLayout());
    frame.add(mazeGenerator, BorderLayout.CENTER);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);


        JButton startButton = new JButton("Start");
        startButton.setPreferredSize(new Dimension(300, 100));
        startButton.setBackground(Color.GREEN); // Set warna latar belakang
        startButton.setOpaque(true); // Aktifkan opsi latar belakang yang berwarna
        startButton.setBorderPainted(false); // Nonaktifkan tampilan border
        startButton.addActionListener(e -> {
        Thread gameThread = new Thread(() -> {
        while (mazeGenerator.redX != mazeGenerator.greenX || mazeGenerator.redY != mazeGenerator.greenY) {
            mazeGenerator.moveRedDroid();
            mazeGenerator.moveGreenDroid();
            mazeGenerator.checkVisibility();
            mazeGenerator.repaint();

            try {
                Thread.sleep(400);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            // Hentikan permainan jika droid berhenti bergerak
            if (mazeGenerator.redX == mazeGenerator.greenX && mazeGenerator.redY == mazeGenerator.greenY) {
                break;
            }

            // Tunggu hingga di-resume jika di-pause
            synchronized (mazeGenerator) {
                while (mazeGenerator.isPaused) {
                    try {
                        mazeGenerator.wait();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                        }
                    }
                }
            }
        });
        gameThread.start();
        });
        
        JButton pauseButton = new JButton("Pause");
        pauseButton.setPreferredSize(new Dimension(150, 50));
        pauseButton.setBackground(Color.YELLOW); // Set warna latar belakang
        pauseButton.setOpaque(true); // Aktifkan opsi latar belakang yang berwarna
        pauseButton.setBorderPainted(false); // Nonaktifkan tampilan border
        pauseButton.addActionListener(e -> {
        mazeGenerator.isPaused = true; // Set isPaused menjadi true
});

        JButton resumeButton = new JButton("Resume");
        resumeButton.setPreferredSize(new Dimension(150, 50));
        resumeButton.setBackground(Color.ORANGE); // Set warna latar belakang
        resumeButton.setOpaque(true); // Aktifkan opsi latar belakang yang berwarna
        resumeButton.setBorderPainted(false); // Nonaktifkan tampilan border
        resumeButton.addActionListener(e -> {
        synchronized (mazeGenerator) {
        mazeGenerator.isPaused = false; // Set isPaused menjadi false
        mazeGenerator.notifyAll(); // Resume permainan
    }
});

       JButton randomizeButton = new JButton("Acak Peta");
        randomizeButton.setPreferredSize(new Dimension(150, 50));
        randomizeButton.setBackground(Color.CYAN); // Set warna latar belakang
        randomizeButton.setOpaque(true); // Aktifkan opsi latar belakang yang berwarna
        randomizeButton.setBorderPainted(false); // Nonaktifkan tampilan border
        randomizeButton.addActionListener(e -> {
            mazeGenerator.generateMaze();
            mazeGenerator.repaint();
        });
        
       JButton addGreenDroidButton = new JButton("Acak Droid Hijau");
        addGreenDroidButton.setPreferredSize(new Dimension(150, 50));
        addGreenDroidButton.setBackground(Color.MAGENTA); // Set warna latar belakang
        addGreenDroidButton.setOpaque(true); // Aktifkan opsi latar belakang yang berwarna
        addGreenDroidButton.setBorderPainted(false); // Nonaktifkan tampilan border
        addGreenDroidButton.addActionListener(e -> {
            mazeGenerator.addGreenDroid(); // Acak droid hijau
            mazeGenerator.repaint(); // Gambar ulang panel
        });
        
        JButton MoveRedDroidButton = new JButton("Acak Droid Merah");
        MoveRedDroidButton.setPreferredSize(new Dimension(150, 50));
        MoveRedDroidButton.setBackground(Color.LIGHT_GRAY); // Set warna latar belakang
        MoveRedDroidButton.setOpaque(true); // Aktifkan opsi latar belakang yang berwarna
        MoveRedDroidButton.setBorderPainted(false); // Nonaktifkan tampilan border
        MoveRedDroidButton.addActionListener(e -> {
            mazeGenerator.MoveRedDroid(); // Acak droid hijau
            mazeGenerator.repaint(); // Gambar ulang panel
        });
        
       JButton addRedDroidButton = new JButton("Tambah Droid Merah");
        addRedDroidButton.setPreferredSize(new Dimension(150, 50));
        addRedDroidButton.setBackground(Color.PINK);
        addRedDroidButton.setOpaque(true);
        addRedDroidButton.setBorderPainted(false);
        addRedDroidButton.addActionListener(e -> {
            mazeGenerator.addRedDroid();
            mazeGenerator.repaint();
        });
        
        JButton stopButton = new JButton("Stop");
        stopButton.setPreferredSize(new Dimension(150, 50));
        stopButton.setBackground(Color.RED); // Set warna latar belakang
        stopButton.setOpaque(true); // Aktifkan opsi latar belakang yang berwarna
        stopButton.setBorderPainted(false); // Nonaktifkan tampilan border
        stopButton.addActionListener(e -> {
            System.exit(0);
        });

       JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.add(startButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Tambahkan spasi vertikal sebesar 10 piksel
        buttonPanel.add(pauseButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Tambahkan spasi vertikal sebesar 10 piksel
        buttonPanel.add(resumeButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Tambahkan spasi vertikal sebesar 10 piksel
        buttonPanel.add(randomizeButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Tambahkan spasi vertikal sebesar 10 piksel
        buttonPanel.add(addRedDroidButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Tambahkan spasi vertikal sebesar 10 piksel
        buttonPanel.add(MoveRedDroidButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Tambahkan spasi vertikal sebesar 10 piksel
        buttonPanel.add(addGreenDroidButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Tambahkan spasi vertikal sebesar 10 piksel
        buttonPanel.add(stopButton);
        frame.add(buttonPanel, BorderLayout.EAST);
    }
}

