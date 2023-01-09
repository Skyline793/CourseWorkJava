import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class SeaBattle extends JFrame {
    GamePanel panel; //объект панели, на которой происходит игры
    private JMenuBar menu; //меню
    private JMenu play; //подменю играть
    private JMenu SaveAndLoad; //подменю сохранение
    private JMenu info; //подменю о программе
    private JMenu PVEmenu; //подменю игрок против пк
    private JMenuItem Autoplacement; //элемент меню авторасстановка
    private JMenuItem UserPlacement; //элемент меню ручная расстановка
    private JMenuItem PVPmenu; //элемент меню игрок против игрока
    private JMenuItem saveItem; //элемент меню сохранить игру
    private JMenuItem loadItem; //элемент меню загрузить игру
    private JMenuItem spravka; //элемент меню справка
    private JMenuItem author; //элемент меню автор


    SeaBattle()
    {
        super("Морской бой");
        panel = new GamePanel();
        Container container = getContentPane();
        container.add(panel);
        setSize(panel.getSize());
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        menu = new JMenuBar();
        play = new JMenu("Играть");
        PVEmenu = new JMenu("Игрок против компьютера");
        PVPmenu = new JMenuItem("Игрок против игрока");
        PVPmenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.StartPVP();
            }
        });
        Autoplacement = new JMenuItem("Авторасстановка");
        Autoplacement.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.StartPVEwithoutPlacement();
            }
        });
        UserPlacement = new JMenuItem("Ручная расстановка");
        UserPlacement.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.StartPVEwithPlacement();
            }
        });
        SaveAndLoad = new JMenu("Сохранение");
        saveItem = new JMenuItem("Сохранить игру");
        saveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.Save();
            }
        });
        loadItem = new JMenuItem("Загрузить игру");
        loadItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.Open();
            }
        });
        info = new JMenu("О программе");
        spravka = new JMenuItem("Справка");
        spravka.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                        "<HTML>Добро пожаловать в \"Морской бой\"!\n\nПравила игры:\nГлавной целью игры является уничтожение всех кораблей на поле противника.\n" +
 "Используйте левую кнопку мыши, чтобы совершить выстрел по выбранной ячейке.\nЕсли в ячейке отобразится желтый крестик - вы попали по палубе корабля " +
"противника и можете повторить ход.\nЕсли вы промахнулись, в ячейке отобразится синяя точка и ход перейдет сопернику.\nПобеждает тот, кто первым уничтожит все корабли противника." +
"\n\nДля начала игры выберите в меню \"Играть\" один из доступных вариантов:\n1. Игрок против компьютера с авторасстановкой. Корабли на вашем поле будут расставлены" +
 "автоматически случайным способом.\n2. Игрок против компьютера с ручной расстановкой. Справа от игровых полей отобразятся корабли, которые вам необходимо расставить:\n" +
 "1 четырехпалубный, 2 трехпалубных, 3 двухпалубных, 4 однопалубных. \nВыберите левой кнопкой мыши корабль, который хотите раместить, затем разместите его на вашем" +
 "поле повторным кликом мыши. \nРазмещать корабли за границей поля, или так, чтобы корабли касались друг друга нельзя! \nКак только вы разместите весь флот, стартует игра.\n" +
"3. Игрок против игрока. Этот режим предназначен для двух игроков. Корабли на обоих полях будут расставлены автоматически. \nИгроки поочередно совершают выстрелы по " +
 "полю противника. Имя атакующего игрока выделено зеленым цветом.\n\nВы можете сохранить ход любой игры, воспользовавшись меню \"Сохранить игру\", а затем " +
 "загрузить ее и продолжить играть с помощью меню \"Загрузить игру\".",
                        "Cправка", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        author = new JMenuItem("Об авторе");
        author.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                        "Разработчик:\nСтудент группы ПИ-11 Тюкавкин И.А.\nАлтГТУ им. И.И. Ползунова\nБарнаул, 2022",
                        "Автор", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        PVEmenu.add(Autoplacement);
        PVEmenu.add(UserPlacement);
        play.add(PVEmenu);
        play.add(PVPmenu);
        menu.add(play);
        SaveAndLoad.add(saveItem);
        SaveAndLoad.add(loadItem);
        menu.add(SaveAndLoad);
        info.add(spravka);
        info.add(author);
        menu.add(info);
        setJMenuBar(menu);
        menu.setBackground(new Color(135, 206, 250));
        menu.setVisible(true);
        try {
            setIconImage(ImageIO.read(getClass().getResource("images/icon.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setVisible(true);
    }
}
