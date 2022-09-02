package com.tictactoe;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "InitServlet", value = "/start")
public class InitServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //создание новой сессии
        HttpSession currentSession = req.getSession(true);

        //создание игрового поля
        Field field = new Field();
        Map<Integer, Sign> fieldData = field.getField();

        //получение списка значений поля
        List<Sign> data = field.getFieldData();

        //добавление в сессию параметров поля (нужно будет для хранения состояния между запросами)
        currentSession.setAttribute("field", field);

        //и значений поля, отсортированных по индексу (нужно для отрисовки крестиков и ноликов)
        currentSession.setAttribute("data", data);

        //перенаправление запроса на страницу index.jsp через сервер
        getServletContext().getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}
