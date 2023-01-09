import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.plaf.basic.BasicTreeUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class GamePanel extends JPanel { //унаследованный класс панель, на которой происходит игра
    private Game game; //ссылка на абстрактный класс игра, с помощью которой будет реализован геймплей
    private GamePVE gamePVE; //ссылка на класс, реализизующий игру в режиме игрок против пк
    private GamePVP gamePVP; //ссылка на класс, реализующий игру в режиме игрок против игрока
    private int gameMode; //логическая переменная, характеризующая режим игры: 1 - PVE, 2 - PVP
    final private int DXY = 70; //смещение от левого верхнего угла при отрисовке
    final private int H = 25; //коэффициент масштаба при отрисовке
    private int s1, s2, s3, s4; //переменные, характеризующие количество кораблей, которое осталось расставить вручную
    private boolean selectS1, selectS2, selectS3, selectS4; //флажки выбора корабля соответствующего типа при расстановке
    private boolean rasstanovka; //логическая переменная, проверяющая выбран ли режим ручной расстановки
    private int vert; //логическая переменная определяющая положение кораблей в ручной расстановке
    private BufferedImage deck, wound, kill, miss, background; //игровые изображения
    final private String[] symbols = new String[] {"А", "Б", "В", "Г", "Д", "Е", "Ж", "З", "И", "К"}; //массив подписей столбцов полей
    final private String[] numbers = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }; //массив подписей строк полей
    Timer timer; //таймер для отрисовки
    private Rectangle field1, field2; //прямоугольники, ограничивающие поля игроков
    private Rectangle ship1, ship2, ship3, ship4; //прямоугольники, соответствующие 4 типам кораблей

    private JButton orientation_button, clear_button; //кнопки повернуть корабли и очистить поле
    private JLabel placelabel, player1Fieldlabel, player2Fieldlabel, countLabel; //надпими

    //конструктор
    GamePanel()
    {
        setSize(950,550);
        gameMode = 0;
        InitializeComponents(); //метод инициализации компонентов панели
        timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(gameMode == 1)
                    DrawComponentsPVE();
                if(gameMode == 2)
                    DrawComponentsPVP();
                EndOfGame();
                repaint(); //перерисовывать панель по таймеру
            }
        });
        //загрузка изображений
        try {
            background = ImageIO.read(getClass().getResource("images/background.jpg"));
            deck = ImageIO.read(getClass().getResource("images/deck.png"));
            wound = ImageIO.read(getClass().getResource("images/wound.png"));
            kill = ImageIO.read(getClass().getResource("images/kill.png"));
            miss = ImageIO.read(getClass().getResource("images/miss.png"));
        }
        catch(java.io.IOException e) {
            e.printStackTrace();
        }
        this.addMouseListener(new Mouse()); //слушатель клика на панель
        // Локализация компонентов окна JFileChooser
        UIManager.put("FileChooser.saveButtonText", "Сохранить");
        UIManager.put("FileChooser.openButtonText", "Открыть");
        UIManager.put("FileChooser.cancelButtonText", "Отмена");
        UIManager.put("FileChooser.fileNameLabelText", "Наименование файла");
        UIManager.put("FileChooser.filesOfTypeLabelText", "Типы файлов");
        UIManager.put("FileChooser.lookInLabelText", "Директория");
        UIManager.put("FileChooser.saveInLabelText", "Сохранить в директории");
        UIManager.put("FileChooser.folderNameLabelText", "Путь директории");
    }

    //метод инициалиации компонентов панели
    private void InitializeComponents()
    {
        setLayout(null);
        orientation_button = new JButton("Повернуть корабли");
        orientation_button.setBackground(Color.WHITE);
        orientation_button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        orientation_button.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
        orientation_button.setBounds(DXY+24*H,DXY+8*H - H/3,7*H,H);
        orientation_button.setVisible(false);
        add(orientation_button);
        orientation_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { //обработчик нажатия на кнопку повернуть корабли
                if(vert == 1)
                    vert = 0;
                else
                    vert = 1;
            }
        });
        
        clear_button = new JButton("Очистить поле");
        clear_button.setBackground(Color.WHITE);
        clear_button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        clear_button.setBounds(DXY+24*H,DXY+9*H,7*H,H);
        clear_button.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
        clear_button.setVisible(false);
        add(clear_button);
        clear_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { //обработчик нажатия на кнопку очистить поле
                game.ClearfirstPlayerField();
                s4 = 1;
                s3 = 2;
                s2 = 3;
                s1 = 4;
            }
        });
        countLabel = new JLabel();
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        countLabel.setBounds(DXY+24*H, DXY + 11*H, 7*H, 3*H);
        countLabel.setBackground(Color.WHITE);
        countLabel.setOpaque(true);
        countLabel.setVisible(false);
        add(countLabel);

        player1Fieldlabel = new JLabel();
        player1Fieldlabel.setHorizontalAlignment(SwingConstants.CENTER);
        player1Fieldlabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        player1Fieldlabel.setBounds(DXY + 2*H, DXY - 2*H-H/4, 6*H, H);
        player1Fieldlabel.setBackground(Color.WHITE);
        player1Fieldlabel.setOpaque(true);
        player1Fieldlabel.setVisible(false);
        add(player1Fieldlabel);

        player2Fieldlabel = new JLabel();
        player2Fieldlabel.setHorizontalAlignment(SwingConstants.CENTER);
        player2Fieldlabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        player2Fieldlabel.setBounds(DXY + 15*H, DXY - 2*H-H/4, 6*H, H);
        player2Fieldlabel.setBackground(Color.WHITE);
        player2Fieldlabel.setOpaque(true);
        player2Fieldlabel.setVisible(false);
        add(player2Fieldlabel);

        placelabel = new JLabel("Расставьте корабли");
        placelabel.setBackground(Color.WHITE);
        placelabel.setOpaque(true);
        placelabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        placelabel.setBounds(DXY+24*H, DXY-H-H/2, 7*H, H);
        placelabel.setHorizontalAlignment(SwingConstants.CENTER);
        placelabel.setVisible(false);
        add(placelabel);

    }

    //перегрузка метода отрисовки панели
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(background, 0,0, this.getWidth(), this.getHeight(), null);
        if(gameMode == 1) {
            DrawFields(g);
            if(!rasstanovka)
                DrawRemainingShips(g);
            DrawPlacementShips(g);
        }
        if(gameMode == 2)
        {
            DrawFields(g);
            DrawRemainingShips(g);
        }
    }

    //метод старта игры против пк с авторасстановкой
    public void StartPVEwithPlacement()
    {
        gameMode = 1;
        gamePVE = new GamePVE();
        game = gamePVE;
        rasstanovka = true;
        vert = 1;
        s4 = 1;
        s3 = 2;
        s2 = 3;
        s1 = 4;
        timer.start();
        game.Start(rasstanovka);
    }

    //метод старта игры против пк с ручной расстановкой
    public void StartPVEwithoutPlacement()
    {
        gameMode = 1;
        gamePVE = new GamePVE();
        game = gamePVE;
        rasstanovka = false;
        timer.start();
        game.Start(rasstanovka);
    }

    //метод старта игры против игрока
    public void StartPVP()
    {
        placelabel.setVisible(false);
        orientation_button.setVisible(false);
        clear_button.setVisible(false);
        gameMode = 2;
        gamePVP = new GamePVP();
        game = gamePVP;
        timer.start();
        game.Start(false);
    }

    //метод отрисовки игровых полей и заполнения их в соответствии с значениями массива
    private void DrawFields(Graphics g) //метод отрисовки игровых полей
    {
        Graphics2D g2 = (Graphics2D)g;
        g2.setStroke(new BasicStroke(3));
        //отрисовка границ
        field1 = new Rectangle(DXY, DXY, 10 * H, 10 * H);
        field2 = new Rectangle(DXY + 13 * H, DXY, 10 * H, 10 * H);
        g2.draw(field1);
        g2.draw(field2);
        g2.setColor(Color.WHITE);
        g2.fill(field1);
        g2.fill(field2);

        g2.setStroke(new BasicStroke(1));
        g2.setColor(Color.GRAY);
        //отрисовка линий
        for (int i = DXY; i <= DXY + 10 * H; i += H)
        {
            g2.drawLine(DXY, i, DXY + 10 * H, i);
            g2.drawLine(i, DXY, i, DXY + 10 * H);
            g2.drawLine(DXY + 13 * H, i, DXY + 23 * H, i);
            g2.drawLine(i + 13 * H, DXY, i + 13 * H, DXY + 10 * H);
        }

        //отрисовка нумерации строк и столбцов
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
        g2.setStroke(new BasicStroke());
        for (int i = 0; i < 10; i++)
        {
            g2.drawString(symbols[i], DXY + i * H + H / 3, DXY - H/2);
            g2.drawString(symbols[i], DXY + 13 * H + i * H + H / 3, DXY - H/2);
            g2.drawString(numbers[i], DXY - H - 6, DXY + i * H + H - H/3);
            g2.drawString(numbers[i], DXY + 12 * H - 6, DXY + i * H + H - H/3);
        }

        //отрисовка ячеек полей на основании массивов
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 10; j++)
            {
                if (gameMode == 1 && game.FirstPlayerValue(i, j) >= 1 && game.FirstPlayerValue(i, j) <= 4)
                    g2.drawImage(deck, DXY + H * i, DXY + H * j, H, H, null);
                if (game.FirstPlayerValue(i, j) >= 8 && game.FirstPlayerValue(i, j) <= 11)
                    g2.drawImage(wound, DXY + H * i, DXY + H * j, H, H, null);
                if (game.FirstPlayerValue(i, j) >= 15 && game.FirstPlayerValue(i, j) <= 18)
                    g2.drawImage(kill, DXY + H * i, DXY + H * j, H, H, null);
                if (game.FirstPlayerValue(i, j) == -2 || game.FirstPlayerValue(i, j) >= 5 && game.FirstPlayerValue(i, j) <= 7)
                    g2.drawImage(miss, DXY + H * i, DXY + H * j, H, H, null);

                if (game.SecondPlayerValue(i, j) >= 8 && game.SecondPlayerValue(i, j) <= 11)
                    g2.drawImage(wound, DXY + 13 * H + H * i, DXY + H * j, H, H, null);
                if (game.SecondPlayerValue(i, j) >= 15 && game.SecondPlayerValue(i, j) <= 18)
                    g2.drawImage(kill, DXY + 13 * H + H * i, DXY + H * j, H, H,null);
                if (game.SecondPlayerValue(i, j) >= 5 && game.SecondPlayerValue(i, j) <= 7 || game.SecondPlayerValue(i, j) == -2)
                    g2.drawImage(miss, DXY + 13 * H + H * i, DXY + H * j, H, H, null);
                if (game.IsEndGame() != 0 && game.SecondPlayerValue(i, j) >= 1 && game.SecondPlayerValue(i, j) <= 4)
                    g2.drawImage(deck, DXY + 13 * H + H * i, DXY + H * j, H, H, null);
            }
    }

    //метод корректировки надписей под режим ПВЕ
    private void DrawComponentsPVE() //метод отрисовки элементов в режиме игрок против пк
    {
        player1Fieldlabel.setForeground(Color.BLACK);
        player2Fieldlabel.setForeground(Color.BLACK);
        player1Fieldlabel.setText("Игрок");
        player2Fieldlabel.setText("Компьютер");
        player1Fieldlabel.setVisible(true);
        player2Fieldlabel.setVisible(true);
        if(rasstanovka == false)
        {
            countLabel.setText("<html>Ходов сделано<br>Игрок: " + String.valueOf(game.GetFirstPlayerCount()) + "<br>Компьютер: " + String.valueOf(game.GetSecondPlayerCount()) + "</html>");
            countLabel.setVisible(true);
        }
        else
            countLabel.setVisible(false);
    }

    //метод корректировки надписей под режим ПВП
    private void DrawComponentsPVP()
    {
        if(game.IsFirstPlayerMove())
        {
            player1Fieldlabel.setForeground(Color.GREEN);
            player2Fieldlabel.setForeground(Color.BLACK);
        }
        if(game.IsSecondPlayerMove())
        {
            player2Fieldlabel.setForeground(Color.GREEN);
            player1Fieldlabel.setForeground(Color.BLACK);
        }
        player1Fieldlabel.setText("Игрок 1");
        player2Fieldlabel.setText("Игрок 2");
        countLabel.setText("<html>Ходов сделано<br>Игрок 1: " + String.valueOf(game.GetFirstPlayerCount()) + "<br>Игрок 2: " + String.valueOf(game.GetSecondPlayerCount()) + "</html>");
        player1Fieldlabel.setVisible(true);
        player2Fieldlabel.setVisible(true);
        countLabel.setVisible(true);
    }

    //метод отрисовки кораблей для ручной расстановки
    private void DrawPlacementShips(Graphics g)
    {
        Graphics2D g2 = (Graphics2D)g;
        if (rasstanovka) {
            placelabel.setVisible(true);
            orientation_button.setVisible(true);
            clear_button.setVisible(true);
            if (vert == 1) {
                ship4 = new Rectangle(DXY + 24 * H, DXY, 4 * H, H);
                ship3 = new Rectangle(DXY + 24 * H, DXY + 2 * H, 3 * H, H);
                ship2 = new Rectangle(DXY + 24 * H, DXY + 4 * H, 2 * H, H);
                ship1 = new Rectangle(DXY + 24 * H, DXY + 6 * H, 1 * H, H);
            } else if(vert == 0)
            {
                ship4 = new Rectangle(DXY + 24 * H, DXY, H, 4 * H);
                ship3 = new Rectangle(DXY + 26 * H, DXY, H, 3 * H);
                ship2 = new Rectangle(DXY + 28 * H, DXY, H, 2 * H);
                ship1 = new Rectangle(DXY + 30 * H, DXY, H, 1 * H);
            }
            if (s4 != 0) {
                g2.setColor(Color.WHITE);
                g2.fill(ship4);
                if (selectS4)
                {
                    g2.setStroke(new BasicStroke(4));
                    g2.setColor(new Color(24, 134, 45));
                    g2.draw(ship4);
                }
                else {
                    g2.setStroke(new BasicStroke(2));
                    g2.setColor(Color.BLACK);
                    g2.draw(ship4);
                }
            }
            if (s3 != 0) {
                g2.setColor(Color.WHITE);
                g2.fill(ship3);
                if (selectS3)
                {
                    g2.setStroke(new BasicStroke(4));
                    g2.setColor(new Color(24, 134, 45));
                    g2.draw(ship3);
                }
                else {
                    g2.setStroke(new BasicStroke(2));
                    g2.setColor(Color.BLACK);
                    g2.draw(ship3);
                }
            }
            if (s2 != 0) {
                g2.setColor(Color.WHITE);
                g2.fill(ship2);
                if (selectS2)
                {
                    g2.setStroke(new BasicStroke(4));
                    g2.setColor(new Color(24, 134, 45));
                    g2.draw(ship2);
                }
                else {
                    g2.setStroke(new BasicStroke(2));
                    g2.setColor(Color.BLACK);
                    g2.draw(ship2);
                }
            }
            if (s1 != 0) {
                g2.setColor(Color.WHITE);
                g2.fill(ship1);
                if (selectS1)
                {
                    g2.setStroke(new BasicStroke(4));
                    g2.setColor(new Color(24, 134, 45));
                    g2.draw(ship1);
                }
                else {
                    g2.setStroke(new BasicStroke(2));
                    g2.setColor(Color.BLACK);
                    g2.draw(ship1);
                }
            }
            if (s1 + s2 + s3 + s4 == 0)
                rasstanovka = false;
        }
        else
        {
            placelabel.setVisible(false);
            orientation_button.setVisible(false);
            clear_button.setVisible(false);
        }
    }

    //метод отрисовки количества оставшихся у игроков кораблей
    private void DrawRemainingShips(Graphics g)
    {
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.fillRect(DXY, DXY + 11 * H, 4 * H, H);
        g2.fillRect(DXY, DXY + 12 * H + 10, 3 * H, H);
        g2.fillRect(DXY, DXY + 13 * H + 20, 2 * H, H);
        g2.fillRect(DXY, DXY + 14 * H + 30, H, H);

        g2.fillOval(DXY + 4 * H + H / 2, DXY + 11 * H, H, H);
        g2.fillOval(DXY + 3 * H + H / 2, DXY + 12 * H + 10, H, H);
        g2.fillOval(DXY + 2 * H + H / 2, DXY + 13 * H + 20, H, H);
        g2.fillOval(DXY + H + H / 2, DXY + 14 * H + 30, H, H);

        g2.fillRect(DXY + 13 * H, DXY + 11 * H, 4 * H, H);
        g2.fillRect(DXY + 13 * H, DXY + 12 * H + 10, 3 * H, H);
        g2.fillRect(DXY + 13 * H, DXY + 13 * H + 20, 2 * H, H);
        g2.fillRect(DXY + 13 * H, DXY + 14 * H + 30, H, H);

        g2.fillOval(DXY + 17 * H + H / 2, DXY + 11 * H, H, H);
        g2.fillOval(DXY + 16 * H + H / 2, DXY + 12 * H + 10, H, H);
        g2.fillOval(DXY + 15 * H + H / 2, DXY + 13 * H + 20, H, H);
        g2.fillOval(DXY + 14 * H + H / 2, DXY + 14 * H + 30, H, H);

        g2.setColor(Color.BLACK);

        g2.drawRect(DXY, DXY + 11 * H, 4 * H, H);
        g2.drawRect(DXY, DXY + 12 * H + 10, 3 * H, H);
        g2.drawRect(DXY, DXY + 13 * H + 20, 2 * H, H);
        g2.drawRect(DXY, DXY + 14 * H + 30, H, H);

        g2.drawOval(DXY + 4 * H + H / 2, DXY + 11 * H, H, H);
        g2.drawOval(DXY + 3 * H + H / 2, DXY + 12 * H + 10, H, H);
        g2.drawOval(DXY + 2 * H + H / 2, DXY + 13 * H + 20, H, H);
        g2.drawOval(DXY + H + H / 2, DXY + 14 * H + 30, H, H);

        g2.drawRect(DXY + 13 * H, DXY + 11 * H, 4 * H, H);
        g2.drawRect(DXY + 13 * H, DXY + 12 * H + 10, 3 * H, H);
        g2.drawRect(DXY + 13 * H, DXY + 13 * H + 20, 2 * H, H);
        g2.drawRect(DXY + 13 * H, DXY + 14 * H + 30, H, H);

        g2.drawOval(DXY + 17 * H + H / 2, DXY + 11 * H, H, H);
        g2.drawOval(DXY + 16 * H + H / 2, DXY + 12 * H + 10, H, H);
        g2.drawOval(DXY + 15 * H + H / 2, DXY + 13 * H + 20, H, H);
        g2.drawOval(DXY + 14 * H + H / 2, DXY + 14 * H + 30, H, H);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
        g2.setColor(new Color(24, 134, 45));
        int[] player1count = game.GetFirstPlayerKillCount();
        g2.drawString(String.valueOf(1 - player1count[0]), DXY + 5 * H - H/4, DXY + 12 * H - H/4);
        g2.drawString(String.valueOf(2 - player1count[1]), DXY + 4 * H - H/4, DXY + 13 * H + 5);
        g2.drawString(String.valueOf(3 - player1count[2]), DXY + 3 * H - H/4, DXY + 14 * H + 15);
        g2.drawString(String.valueOf(4 - player1count[3]), DXY + 2 * H - H/4, DXY + 15 * H + 25);

        g2.setColor(Color.RED);
        int[] player2count = game.GetSecondPlayerKillCount();
        g2.drawString(String.valueOf(1 - player2count[0]), DXY + 17 * H + 20, DXY + 12 * H - H/4);
        g2.drawString(String.valueOf(2 - player2count[1]), DXY + 16 * H + 20, DXY + 13 * H + 5);
        g2.drawString(String.valueOf(3 - player2count[2]), DXY + 15 * H + 20, DXY + 14 * H + 15);
        g2.drawString(String.valueOf(4 - player2count[3]), DXY + 14 * H + 20, DXY + 15 * H + 25);
    }

    //метод реализации ручной расстановки корабля
    private void PlaceShip(MouseEvent e)
    {
        if (ship4.contains(e.getPoint()))
        {
            selectS4 = true;
            selectS3 = false;
            selectS2 = false;
            selectS1 = false;
        }
        if (ship3.contains(e.getPoint()))
        {
            selectS4 = false;
            selectS3 = true;
            selectS2 = false;
            selectS1 = false;
        }
        if (ship2.contains(e.getPoint()))
        {
            selectS4 = false;
            selectS3 = false;
            selectS2 = true;
            selectS1 = false;
        }
        if (ship1.contains(e.getPoint()))
        {
            selectS4 = false;
            selectS3 = false;
            selectS2 = false;
            selectS1 = true;
        }
        if (field1.contains(e.getPoint()))
        {
            int mX = e.getX();
            int mY = e.getY();
            int i = (mX - (DXY)) / H;
            int j = (mY - DXY) / H;
            if (s4 != 0 && selectS4)
            {
                selectS4 = false;
                if (game.FirstPlayerPlacement(i, j, 4, vert))
                    s4--;
            }
            if (s3 != 0 && selectS3)
            {
                selectS3 = false;
                if (game.FirstPlayerPlacement(i, j, 3, vert))
                    s3--;
            }
            if (s2 != 0 && selectS2)
            {
                selectS2 = false;
                if (game.FirstPlayerPlacement(i, j, 2, vert))
                    s2--;
            }
            if (s1 != 0 && selectS1)
            {
                selectS1 = false;
                if (game.FirstPlayerPlacement(i, j, 1, vert))
                    s1--;
            }
        }
    }

    //метод обработки клика мышкой по панели
    private void UserClick(MouseEvent e)
    {
        switch (gameMode)
        {
            case 1:
                if (game.IsEndGame() == 0 && !rasstanovka && game.IsFirstPlayerMove())
                {
                    if (field2.contains(e.getPoint()))
                    {
                        int mX = e.getX();
                        int mY = e.getY();
                        int i = (mX - (DXY + 13 * H)) / H;
                        int j = (mY - DXY) / H;
                        if (game.SecondPlayerValue(i, j) >= -1 && game.SecondPlayerValue(i, j) <= 4) {
                            game.PlayerMove(i, j);
                        }
                    }
                }
                if (rasstanovka)
                    PlaceShip(e);
                break;
            case 2:
                if (game.IsEndGame() == 0 && game.IsFirstPlayerMove() && field2.contains(e.getPoint()))
                {
                    int mX = e.getX();
                    int mY = e.getY();
                    int i = (mX - (DXY + 13 * H)) / H;
                    int j = (mY - DXY) / H;
                    if (game.SecondPlayerValue(i, j) >= -1 && game.SecondPlayerValue(i, j) <= 4) {
                        game.PlayerMove(i, j);
                    }
                }
                if (game.IsEndGame() == 0 && game.IsSecondPlayerMove() && field1.contains(e.getPoint()))
                {
                    int mX = e.getX();
                    int mY = e.getY();
                    int i = (mX - DXY) / H;
                    int j = (mY - DXY) / H;
                    if (game.FirstPlayerValue(i, j) >= -1 && game.FirstPlayerValue(i, j) <= 4) {
                        game.PlayerMove(i, j);
                    }
                }
                break;
        }
    }

    //класс, реализующий обработчик клика мышкой по панели
    private class Mouse extends MouseAdapter
    {
        @Override
        public void mouseClicked(MouseEvent e) {
            if(e.getClickCount() == 1 && e.getButton() == 1)
                UserClick(e);
        }
    }

    //метод сохранения игры
    public void Save()
    {
        try
        {
            if (rasstanovka) throw new Exception("Игру нельзя сохранить во время расстановки");
            JFileChooser saver = new JFileChooser("C:\\");
            FileNameExtensionFilter filter = new FileNameExtensionFilter(".bin", "bin");
            saver.setFileFilter(filter);
            saver.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (saver.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                String filename = saver.getSelectedFile().getPath();
                FileOutputStream fos;
                ObjectOutputStream oos;

                fos = new FileOutputStream(filename);
                oos = new ObjectOutputStream(fos);
                oos.writeObject(game);
                oos.flush();
                oos.close();
            }
        }
        catch(Exception ex)
        {
            JOptionPane.showMessageDialog(null,
                    "Возникла ошибка при сохранении игры!",
                    "Ошибка", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    //метод загрузки игры
    public void Open() {

        try {
            JFileChooser opener = new JFileChooser("C:\\");
            FileNameExtensionFilter filter = new FileNameExtensionFilter(".bin", "bin");
            opener.setFileFilter(filter);
            opener.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (opener.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                String filename = opener.getSelectedFile().getPath();
                FileInputStream fis;
                ObjectInputStream ois;
                fis = new FileInputStream(filename);
                ois = new ObjectInputStream(fis);
                Object obj = ois.readObject();
                if (obj instanceof GamePVE) {
                    gameMode = 1;
                    game = (GamePVE) obj;
                }
                if (obj instanceof GamePVP) {
                    gameMode = 2;
                    game = (GamePVP) obj;
                }
                rasstanovka = false;
                repaint();
                timer.start();
            }
        }
        catch(Exception ex)
        {
            JOptionPane.showMessageDialog(null,
                    "Возникла ошибка при загрузке сохранения!",
                    "Ошибка", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    //метод, проверяющий игру на завершение
    private void EndOfGame()
    {
        if(game.IsEndGame() == 1)
        {
            timer.stop();
            if(gameMode == 1)
            {
                JOptionPane.showMessageDialog(null,
                        "Подравляем! Вы победили!",
                        "Победа", JOptionPane.INFORMATION_MESSAGE);
            }
            if(gameMode == 2)
            {
                JOptionPane.showMessageDialog(null,
                        "Подравляем! Игрок 1 победил!",
                        "Поражение", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        if(game.IsEndGame() == 2)
        {
            timer.stop();
            if(gameMode == 1)
            {
                JOptionPane.showMessageDialog(null,
                        "К сожалению, Вы проиграли!",
                        "Поражение", JOptionPane.INFORMATION_MESSAGE);
            }
            if(gameMode == 2)
            {
                JOptionPane.showMessageDialog(null,
                        "Поздравляем! Игрок 2 победил!",
                        "Победа", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}
