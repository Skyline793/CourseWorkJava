import java.io.Serializable;

public class GamePVP extends Game implements Serializable
{
    /*метод хода игрока
    @param i - номер строки
    @param j - номер столбца*/
    public void PlayerMove(int i, int j)
{
    if (IsFirstPlayerMove()) //если время хода игрока 1
    {
        firstPlayerCount++;
        secondPlayerField.Fire(i, j);
        secondPlayerField.TestKilled(i, j);
        if (secondPlayerField.GetValue(i, j) < 8) //если игрок 1 не попал
        {

            firstPlayerMove = false;
            secondPlayerMove = true;
        }
    }
    else if (IsSecondPlayerMove()) //если время хода игрока 2
    {
        secondPlayerCount++;
        firstPlayerField.Fire(i, j);
        firstPlayerField.TestKilled(i, j);
        if (firstPlayerField.GetValue(i, j) < 8) //если игрок 2 не попал
        {
            secondPlayerMove = false;
            firstPlayerMove = true;
        }
    }
}
}
