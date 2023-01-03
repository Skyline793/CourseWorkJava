import static java.lang.Math.random;

public class Battlefield {

    private static int N = 10;
    private int[][] field;
    public Battlefield()
    {
        field = new int[N][N];
    }
    /*метод симуляции выстрела по полю
    @param i - номер строки
    @param j - номер столбца*/
    public void Fire(int i, int j)
    {
        field[i][j] += 7;
    }

    /*метод получения значения ячейки поля
    @param i - номер строки
    @param j - номер столбца
    @return значение ячейки*/
    public int GetValue(int i, int j)
    {
        return field[i][j];
    }

    /*метод, проверяющий принадлежит ли ячейка с заданными координатами полю
    @param i - номер строки
    @param j - номер столбца
    @return true, если принадлежит, false, если нет*/
    public boolean InField(int i, int j)
    {
        if (i >= 0 && i < N && j >= 0 && j < N)
            return true;
        else
            return false;
    }

    /*метод, проверяющий убийство корабля
    @param i - номер строки
    @param j - номер столбца*/
    public void TestKilled(int i, int j)
    {
        if (field[i][j] == 8)
        {
            field[i][j] += 7;
            SurroundKilledDeck(i, j);
        }
        if (field[i][j] == 9)
            AnalizeKill(i, j, 2);
        if (field[i][j] == 10)
            AnalizeKill(i, j, 3);
        if (field[i][j] == 11)
            AnalizeKill(i, j, 4);
    }

    /*метод, анализирующий, убит ли корабль, координаты подбитой палубы которого передаются через параметры
    @param i - номер строки
    @param j - номер столбца*
    @param deckCount - количество палуб корабля*/
    public void AnalizeKill(int i, int j, int deckCount)
    {
        int woundCount = 0; //число подбитых палуб
        //считаем количество раненых палуб
        for (int x = i - (deckCount - 1); x <= i + (deckCount - 1); x++)
            for (int y = j - (deckCount - 1); y <= j + (deckCount - 1); y++)
            {
                if (InField(x, y))
                    if (field[x][y] == deckCount + 7)
                woundCount++;
            }
        if (woundCount == deckCount) //если число подбитых палуб равно общему числу палуб
            //преобразуем корабль в убитый и изменяем окружение
            for (int x = i - (deckCount - 1); x <= i + (deckCount - 1); x++)
                for (int y = j - (deckCount - 1); y <= j + (deckCount - 1); y++)
                {
                    if (InField(x, y))
                        if (field[x][y] == deckCount + 7)
                    {
                        field[x][y] += 7;
                        SurroundKilledDeck(x, y);
                    }
                }
    }

    /*метод, уменьшающий окружение убитой палубы на 1 с помощью метода SetSurroundingKilled
    @param i - номер строки
    @param j - номер столбца*/
    private void SurroundKilledDeck(int i, int j)
    {
        SetSurroundingKilled(i - 1, j - 1);
        SetSurroundingKilled(i - 1, j);
        SetSurroundingKilled(i - 1, j + 1);
        SetSurroundingKilled(i, j + 1);
        SetSurroundingKilled(i + 1, j + 1);
        SetSurroundingKilled(i + 1, j);
        SetSurroundingKilled(i + 1, j - 1);
        SetSurroundingKilled(i, j - 1);
    }

    /*метод, уменьшающий значение ячейки поля на 1, если оно равно -1 или 6
    @param i - номер строки
    @param j - номер столбца*/
    private void SetSurroundingKilled(int i, int j)
    {
        if (InField(i, j))
            if (field[i][j] == -1 || field[i][j] == 6)
        field[i][j]--;
    }

    /*метод, проверяющий, можно ли расположить в заданной ячейке новую палубу
    @param i - номер строки
    @param j - номер столбца
    @return - true, если можно, false, если нельзя*/
    private boolean TestNewDeck(int i, int j)
    {
        if (InField(i, j))
        {
            if (field[i][j] == 0 || field[i][j] == -2)
                return true;
		else return false;
        }
        else return false;
    }

    /*метод для случайной генерации корабля с заданным числом палуб
    @param deckCount - число палуб корабля*/
    private void ShipAutoPlacement(int deckCount)
    {
        int i, j; //координаты начала корабля
        int direction; //ориентация корабля
        boolean flag = false; //флаг проверки возможности расположить палубу
        while (true)
        {
            i = (int)(random() * 10);
            j = (int)(random() * 10);
            direction = (int)(random() * 4); //0 вверх, 1 вправо, 2 вниз, 3 влево
            if (TestNewDeck(i, j))
                switch (direction)
                {
                    case 0:
                        if (TestNewDeck(i - deckCount - 1, j))
                            flag = true;
                        break;
                    case 1:
                        if (TestNewDeck(i, j + deckCount - 1))
                            flag = true;
                        break;
                    case 2:
                        if (TestNewDeck(i + deckCount - 1, j))
                            flag = true;
                        break;
                    case 3:
                        if (TestNewDeck(i, j - deckCount - 1))
                            flag = true;
                        break;
                }
            if (flag)
                break;
        }
        field[i][j] = deckCount;
        SurroundPlacedDeck(i, j);
        switch (direction)
        {
            case 0:
                for (int k = deckCount - 1; k >= 1; k--)
                {
                    field[i - k][j] = deckCount;
                    SurroundPlacedDeck(i - k, j);
                }
                break;
            case 1:
                for (int k = deckCount - 1; k >= 1; k--)
                {
                    field[i][j + k] = deckCount;
                    SurroundPlacedDeck(i, j + k);
                }
                break;
            case 2:
                for (int k = deckCount - 1; k >= 1; k--)
                {
                    field[i + k][j] = deckCount;
                    SurroundPlacedDeck(i + k, j);
                }
                break;
            case 3:
                for (int k = deckCount - 1; k >= 1; k--)
                {
                    field[i][j - k] = deckCount;
                    SurroundPlacedDeck(i, j - k);
                }
                break;
        }
        FinishSurrounding();
    }

    /*метод, выполняющий полную генерацию игрового поля с помощью метода ShipAutoPlacement*/
    public void FullAutoPlacement()
    {
        ShipAutoPlacement(4);
        for (int i = 1; i <= 2; i++)
            ShipAutoPlacement(3);
        for (int i = 1; i <= 3; i++)
            ShipAutoPlacement(2);
        for (int i = 1; i <= 4; i++)
            ShipAutoPlacement(1);
    }

    /*метод, изменяющий значение ячейки поля на -2, если оно равно 0 (пустое пространство)
    @param i - номер строки
    @param j - номер столбца*/
    private void SetSurroundingPlaced(int i, int j)
    {
        if (InField(i, j))
            if (field[i][j] == 0)
        field[i][j] = -2;
    }

    /*метод, изменяющий окружение размещенной палубы
    @param i - номер строки
    @param j - номер столбца*/
    private void SurroundPlacedDeck(int i, int j)
    {
        SetSurroundingPlaced(i - 1, j - 1);
        SetSurroundingPlaced(i - 1, j);
        SetSurroundingPlaced(i - 1, j + 1);
        SetSurroundingPlaced(i, j + 1);
        SetSurroundingPlaced(i + 1, j + 1);
        SetSurroundingPlaced(i + 1, j);
        SetSurroundingPlaced(i + 1, j - 1);
        SetSurroundingPlaced(i, j - 1);
    }

    /*метод, завершающий расстановку корабля*/
    private void FinishSurrounding()
    {
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                if (field[i][j] == -2)
        field[i][j]++;
    }

    /*метод ручной расстановки корабля
    @param i - номер строки
    @param j - номер столбца
    @param deckCount - количество палуб корабля
    @param direction -  ориетация корабля
    @return - 1, если корабль размещен, 0, если нет*/
    public boolean UserPlacement(int i, int j, int deckCount, int direction)
    {
        boolean flag = false; //флаг проверки возможности разместить палубу
        if (TestNewDeck(i, j))
        {
            switch (direction)
            {
                case 0:
                    if (TestNewDeck(i, j + deckCount - 1))
                        flag = true;
                    break;
                case 1:
                    if (TestNewDeck(i + deckCount - 1, j))
                        flag = true;
                    break;
            }
        }
        if (!flag) return false;
        field[i][j] = deckCount;
        SurroundPlacedDeck(i, j);
        switch (direction)
        {
            case 0:
                for (int k = deckCount - 1; k >= 1; k--)
                {
                    field[i][j + k] = deckCount;
                    SurroundPlacedDeck(i, j + k);
                }
                break;
            case 1:
                for (int k = deckCount - 1; k >= 1; k--)
                {
                    field[i + k][j] = deckCount;
                    SurroundPlacedDeck(i + k, j);
                }
                break;
        }
        FinishSurrounding();
        return true;
    }

    /*метод получения суммы значений убитых палуб
    @return - сумма значений убитых палуб*/
    public int SumKilled()
    {
        int Sum = 0;
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                if (field[i][j] >= 15)
        Sum += field[i][j];
        return Sum;
    }

    /*метод очистки поля*/
    public void Clear()
    {
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                field[i][j] = 0;
    }

/*метод получения количества убитых кораблей каждого типа
@return - массив количества убитых корабей каждого типа*/
    int[] GetKillCount()
    {
        int[] count;
        int s1 = 0, s2 = 0, s3 = 0, s4 = 0;
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
            {
                if (field[i][j] == 15)
                    s1++;
                if (field[i][j] == 16)
                    s2++;
                if (field[i][j] == 17)
                    s3++;
                if (field[i][j] == 18)
                    s4++;
            }
        s2 /= 2;
        s3 /= 3;
        s4 /= 4;
        count = new int[] {s4, s3, s2, s1};
        return count;
    }

}
