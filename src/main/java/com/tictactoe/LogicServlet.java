package com.tictactoe;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "LogicServlet", value = "/logic")
public class LogicServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //получаем текущую сессию
        HttpSession currentSession = req.getSession();

        //получаем объект игрового поля из сессии
        Field field = extractField(currentSession);

        //получаем индекс ячейки, по которой произошел клик
        int index = getSelectedIndex(req);
        Sign currentSign = field.getField().get(index);

        //проверяем, что ячейка, по которой был клик - пустая,
        //иначе ничего не делаем и отправляем пользователя на ту же страницу без изменений
        //параметров сессии
        if (Sign.EMPTY != currentSign) {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/index.jsp");
            dispatcher.forward(req, resp);
            return;
        }

        //ставим крестик в ячейке, по которой кликнул пользователь
        field.getField().put(index, Sign.CROSS);

        //проверяем, не победил ли крестик после добавления последнего клика пользователя
        if (checkWin(resp, currentSession, field)) {
            return;
        }

        //получаем пустую ячейку поля
        int emptyFieldIndex = field.getEmptyFieldIndex();

        if (emptyFieldIndex >= 0) {
            field.getField().put(emptyFieldIndex, Sign.NOUGHT);
            //проверяем, не победил ли нолик после добавления последнего нолика
            if (checkWin(resp, currentSession, field)) {
                return;
            }
        }
        //если пустой ячейки нет и никто не победил - значит это ничья
        else {
            //добавляем в сессию флаг, который сигнализирует что произошла ничья
            currentSession.setAttribute("draw", true);

            //считаем список значков
            List<Sign> data = field.getFieldData();

            //обновляем этот список в сессии
            currentSession.setAttribute("data", data);

            //шлем редирект
            resp.sendRedirect("/index.jsp");
            return;
        }

        //считаем список значков
        List<Sign> data = field.getFieldData();

        //обновляем объект поля и список значков в сессии
        currentSession.setAttribute("data", data);
        currentSession.setAttribute("field", field);

        resp.sendRedirect("/index.jsp");
    }

    private int getSelectedIndex(HttpServletRequest request) {
        String click = request.getParameter("click");
        boolean isNumeric = click.chars().allMatch(Character::isDigit);
        return isNumeric ? Integer.parseInt(click) : 0;
    }

    private Field extractField(HttpSession currentSession) {
        Object fieldAttribute = currentSession.getAttribute("field");
        if (Field.class != fieldAttribute.getClass()) {
            currentSession.invalidate();
            throw new RuntimeException("Session is broken, try one more time");
        }
        return (Field) fieldAttribute;
    }

    /**
     * метод проверяет, нет ли трех крестиков/ноликов в ряд
     * возвращает true/false
     */
    private boolean checkWin(HttpServletResponse response, HttpSession currentSession, Field field) throws IOException {
        Sign winner = field.checkWin();
        if (Sign.CROSS == winner || Sign.NOUGHT == winner) {
            //добавляем флаг, который показывает что кто-то победил
            currentSession.setAttribute("winner", winner);

            //считаем список значков
            List<Sign> data = field.getFieldData();

            //обновляем этот список в сессии
            currentSession.setAttribute("data", data);

            //шлем редирект
            response.sendRedirect("/index.jsp");
            return true;
        }
        return false;
    }
}
