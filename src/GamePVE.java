import java.io.Serializable;

import static java.lang.Math.random;

public class GamePVE  extends Game implements Serializable {

    /*метод хода игрока
@param i - номер строки
@param j - номер столбца*/
    public void PlayerMove(int i, int j) {
        firstPlayerCount++;
        secondPlayerField.Fire(i, j);
        secondPlayerField.TestKilled(i, j);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if(SecondPlayerValue(i, j) < 8)
                {
                    firstPlayerMove = false;
                    secondPlayerMove = true;
                    while(secondPlayerMove)
                    {
                        try
                        {
                            Thread.sleep(500);
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                        secondPlayerMove = CompMove();
                    }
                    firstPlayerMove = true;
                }
            }
        });
        thread.start();
    }

    /*метод хода компьютера
    @return - 1, если ход произведен успешно, 0, если нет*/
    private boolean CompMove()
    {
        secondPlayerCount++;
        boolean hit = false; //логическая переменная означающая попадание по кораблю
        boolean wound = false; //логическая переменная означающая подбитую палубу
        boolean horiz = false; //флаг поиска корабля по горизонтали
        boolean vert = false; //флаг поиска корабля по вертикали
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 10; j++)
                if (firstPlayerField.GetValue(i, j) >= 9 && firstPlayerField.GetValue(i, j) <= 11) //если ячейка подбитого корабля
                {
                    wound = true;
                    //поиск других подбитых палуб корабля по вертикали
                    if ((firstPlayerField.InField(i, j - 1) && firstPlayerField.GetValue(i, j - 1) >= 9 && firstPlayerField.GetValue(i, j - 1) <= 11)
                            || (firstPlayerField.InField(i, j + 1) && firstPlayerField.GetValue(i, j + 1) >= 9 && firstPlayerField.GetValue(i, j + 1) <= 11)
                            || (firstPlayerField.InField(i, j - 2) && firstPlayerField.GetValue(i, j - 2) >= 9 && firstPlayerField.GetValue(i, j - 2) <= 11)
                            || (firstPlayerField.InField(i, j + 2) && firstPlayerField.GetValue(i, j + 2) >= 9 && firstPlayerField.GetValue(i, j + 2) <= 11)
                            || (firstPlayerField.InField(i, j - 3) && firstPlayerField.GetValue(i, j - 3) >= 9 && firstPlayerField.GetValue(i, j - 3) <= 11)
                            || (firstPlayerField.InField(i, j + 3) && firstPlayerField.GetValue(i, j + 3) >= 9 && firstPlayerField.GetValue(i, j + 3) <= 11))
                        vert = true;
                    //если по вертикали найдена подбитая палуба
                    if (vert)
                    {
                        if (firstPlayerField.InField(i, j + 1) && firstPlayerField.GetValue(i, j + 1) <= 4 && firstPlayerField.GetValue(i, j + 1) != -2)
                        {
                            firstPlayerField.Fire(i, j + 1);
                            firstPlayerField.TestKilled(i, j + 1);
                            if (firstPlayerField.GetValue(i, j + 1) >= 8)
                                hit = true;
                            return hit;
                        }
                        if (firstPlayerField.InField(i, j - 1) && firstPlayerField.GetValue(i, j - 1) <= 4 && firstPlayerField.GetValue(i, j - 1) != -2)
                        {
                            firstPlayerField.Fire(i, j - 1);
                            firstPlayerField.TestKilled(i, j - 1);
                            if (firstPlayerField.GetValue(i, j - 1) >= 8)
                                hit = true;
                            return hit;
                        }
                    }
                    //поиск других подбитых палуб корабля по горизонтали, если не найдены по вертикали
                    if ((firstPlayerField.InField(i - 1, j) && firstPlayerField.GetValue(i - 1, j) >= 9 && firstPlayerField.GetValue(i - 1, j) <= 11)
                            || (firstPlayerField.InField(i + 1, j) && firstPlayerField.GetValue(i + 1, j) >= 9 && firstPlayerField.GetValue(i + 1, j) <= 11)
                            || (firstPlayerField.InField(i - 2, j) && firstPlayerField.GetValue(i - 2, j) >= 9 && firstPlayerField.GetValue(i - 2, j) <= 11)
                            || (firstPlayerField.InField(i + 2, j) && firstPlayerField.GetValue(i + 2, j) >= 9 && firstPlayerField.GetValue(i + 2, j) <= 11)
                            || (firstPlayerField.InField(i - 3, j) && firstPlayerField.GetValue(i - 3, j) >= 9 && firstPlayerField.GetValue(i - 3, j) <= 11)
                            || (firstPlayerField.InField(i + 3, j) && firstPlayerField.GetValue(i + 3, j) >= 9 && firstPlayerField.GetValue(i + 3, j) <= 11))
                        horiz = true;
                    if (horiz)
                    {
                        if (firstPlayerField.InField(i - 1, j) && firstPlayerField.GetValue(i - 1, j) <= 4 && firstPlayerField.GetValue(i - 1, j) != -2)
                        {
                            firstPlayerField.Fire(i - 1, j);
                            firstPlayerField.TestKilled(i - 1, j);
                            if (firstPlayerField.GetValue(i - 1, j) >= 8)
                                hit = true;
                            return hit;
                        }
                        if (firstPlayerField.InField(i + 1, j) && firstPlayerField.GetValue(i + 1, j) <= 4 && firstPlayerField.GetValue(i + 1, j) != -2)
                        {
                            firstPlayerField.Fire(i + 1, j);
                            firstPlayerField.TestKilled(i + 1, j);
                            if (firstPlayerField.GetValue(i + 1, j) >= 8)
                                hit = true;
                            return hit;
                        }
                    }
                    //если вокруг не найдены подбитые палубы
                    if (horiz == false && vert == false)
                        while (true) //бесконечный цикл генерации случайного направления удара
                        {
                            int dir = (int)(random() * 4); //0 вверх, 1 вправо, 2 вниз, 3 влево
                            if (dir == 0 && firstPlayerField.InField(i - 1, j) && firstPlayerField.GetValue(i - 1, j) <= 4 && firstPlayerField.GetValue(i - 1, j) != -2)
                            {
                                firstPlayerField.Fire(i - 1, j);
                                firstPlayerField.TestKilled(i - 1, j);
                                if (firstPlayerField.GetValue(i - 1, j) >= 8)
                                    hit = true;
                                return hit;
                            }
                            if (dir == 1 && firstPlayerField.InField(i, j + 1) && firstPlayerField.GetValue(i, j + 1) <= 4 && firstPlayerField.GetValue(i, j + 1) != -2)
                            {
                                firstPlayerField.Fire(i, j + 1);
                                firstPlayerField.TestKilled(i, j + 1);
                                if (firstPlayerField.GetValue(i, j + 1) >= 8)
                                    hit = true;
                                return hit;
                            }
                            if (dir == 2 && firstPlayerField.InField(i + 1, j) && firstPlayerField.GetValue(i + 1, j) <= 4 && firstPlayerField.GetValue(i + 1, j) != -2)
                            {
                                firstPlayerField.Fire(i + 1, j);
                                firstPlayerField.TestKilled(i + 1, j);
                                if (firstPlayerField.GetValue(i + 1, j) >= 8)
                                    hit = true;
                                return hit;
                            }
                            if (dir == 3 && firstPlayerField.InField(i, j - 1) && firstPlayerField.GetValue(i, j - 1) <= 4 && firstPlayerField.GetValue(i, j - 1) != -2)
                            {
                                firstPlayerField.Fire(i, j - 1);
                                firstPlayerField.TestKilled(i, j - 1);
                                if (firstPlayerField.GetValue(i, j - 1) >= 8)
                                    hit = true;
                                return hit;
                            }
                        }
                }
        //если на поле не найдено подбитых палуб
        if (wound == false)
        {
            while (true)
            {
                // Находим случайную позицию на игровом поле
                int i = (int)(random() * 10);
                int j = (int)(random() * 10);
                //Проверяем, что можно сделать выстрел
                if ((firstPlayerField.GetValue(i, j) <= 4) && (firstPlayerField.GetValue(i, j) != -2))
                {
                    firstPlayerField.Fire(i, j);
                    firstPlayerField.TestKilled(i, j);
                    if (firstPlayerField.GetValue(i, j) >= 8) {
                        hit = true;
                    }
                    return hit;
                }
            }
        }
        return hit;
    }
}
