import java.io.Serializable;

import static java.lang.Math.random;

abstract public class Game implements Serializable {
    protected Battlefield firstPlayerField, secondPlayerField; //объекты поле игрока и поле пк
    protected int endGame; //0 игра идет, 1 игрок победил, 2 компьютер победил
    protected int firstPlayerCount; //число ходов игрока
    protected int secondPlayerCount; //число ходов пк
    protected boolean firstPlayerMove; //логическая переменная, означающая ход игрока
    protected boolean secondPlayerMove; //логическая переменная, означающая ход пк

    public Game()
    {
        firstPlayerField = new Battlefield();
        secondPlayerField = new Battlefield();
    }

    /*метод начала игры
    @param rasstanovka - включен ли режим расстановки*/
    public void Start(boolean rasstanovka)
    {
        endGame = 0;
        firstPlayerCount = 0;
        secondPlayerCount = 0;
        firstPlayerMove = true;
        secondPlayerMove = false;
        firstPlayerField.Clear();
        secondPlayerField.Clear();
        if (!rasstanovka)
            firstPlayerField.FullAutoPlacement();
        secondPlayerField.FullAutoPlacement();
    }

    /*метод хода игрока
    @param i - номер строки
    @param j - номер столбца*/
    abstract public void PlayerMove(int i, int j);

    /*метод проверки окончания игры
    @return - 0 - игра продолжается, 1 - победил игрок, 2 - победил пк*/
    public int IsEndGame()
    {
        if (secondPlayerField.SumKilled() == 330) endGame = 1;
        if (firstPlayerField.SumKilled() == 330) endGame = 2;
        if (endGame == 1 || endGame == 2)
        {
            firstPlayerMove = false;
            secondPlayerMove = false;

        }
        return endGame;
    }

    /*метод, возвращающий количество сделанных игроком ходов*/
    public int GetFirstPlayerCount()
    {
        return firstPlayerCount;
    }

    /*метод, возвращающий количество сделанных пк ходов*/
    public int GetSecondPlayerCount()
    {
        return secondPlayerCount;
    }

    /*метод, проверяющий ход ли игрока
    @return - 1, если ход игрока, 0, если ход пк*/
    public boolean IsFirstPlayerMove()
    {
        return firstPlayerMove;
    }

    /*метод, проверяющий ход ли пк
    @return - 1, если ход пк, 0, если ход игрока*/
    public boolean IsSecondPlayerMove()
    {
        return secondPlayerMove;
    }

    //метод очистки поля игрока
    public void ClearfirstPlayerField()
    {
        firstPlayerField.Clear();
    }

    /*метод получения значения ячейки поля игрока
    @param i - номер строки
    @param j - номер столбца
    @return - значение ячейки [i][j] поля игрока*/
    public int FirstPlayerValue(int i, int j)
    {
        return firstPlayerField.GetValue(i, j);
    }

    /*метод получения значения ячейки поля пк
    @param i - номер строки
    @param j - номер столбца
    @return - значение ячейки [i][j] поля пк*/
    public int SecondPlayerValue(int i, int j)
    {
        return secondPlayerField.GetValue(i, j);
    }

    /*метод ручного размещения корабля на поле игрока
    @param i - номер строки
    @param j - номер столбца
    @param deckCount - количество палуб
    @param direction - направление расстановки
    @return - 1, если корабль размещен, 0, если нет*/
    public boolean FirstPlayerPlacement(int i, int j, int deckCount, int direction)
    {
        return firstPlayerField.UserPlacement(i, j, deckCount, direction);
    }

    /*метод получения числа убитых кораблей каждого типа на поле игрока
    @return - массив количеств убитых кораблей*/
    public int[] GetFirstPlayerKillCount()
    {
        int[] count = firstPlayerField.GetKillCount();
        return count;
    }

    /*метод получения числа убитых кораблей каждого типа на поле пк
    @return - массив количеств убитых кораблей*/
    public int[] GetSecondPlayerKillCount()
    {
        int[] count = secondPlayerField.GetKillCount();
        return count;
    }
}
