package br.ufg.persistencia.agendamento_vacinacao.controller;

import br.ufg.persistencia.agendamento_vacinacao.dao.*;
import br.ufg.persistencia.agendamento_vacinacao.model.Agenda;
import br.ufg.persistencia.agendamento_vacinacao.model.TipoSituacao;
import br.ufg.persistencia.agendamento_vacinacao.model.Usuario;
import br.ufg.persistencia.agendamento_vacinacao.model.Vacina;
import br.ufg.persistencia.agendamento_vacinacao.util.StringUtil;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@WebServlet("/usuario/*")
public class ControleUsuario extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private EntityManager en;
    private DaoAlergia daoAlergia;
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sPath = request.getPathInfo();
        switch (sPath) {
            case "/inserir":
                insert(request, response);
                break;
            case "/atualizar":
                try {
                    update(request, response);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
        }
        response.sendRedirect("listar");
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String sPath = request.getPathInfo();
        switch (sPath) {
            case "/inserir":
                getInsert(request,response);
                break;
            case "/atualizar":
                getUpdate(request,response);
                break;
            case "/remover":
                delete(request,response);
                break;
            case "/listar":
                getList(request,response);
                break;
            default:
                getList(request,response);
                break;
        }
    }
    protected void insert(HttpServletRequest request, HttpServletResponse response){
        Usuario usuario = new Usuario();
        usuario.setNome( request.getParameter("nome"));
        usuario.setDataNasc(request.getParameter("data_nasc"));
        usuario.setSexo( request.getParameter("sexo"));
        usuario.setLogradouro( request.getParameter("logradouro"));
        usuario.setNumero( Integer.valueOf(request.getParameter("numero")));
        usuario.setSetor( request.getParameter("setor"));
        usuario.setCidade( request.getParameter("cidade"));
        usuario.setUf( request.getParameter("uf"));
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        java.sql.Date date = null;
        try {
            date = new java.sql.Date(dateFormat.parse(usuario.getDataNasc()).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    @SneakyThrows
    protected void update(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Usuario usuario = new Usuario();
        usuario.setNome( request.getParameter("nome"));
        usuario.setDataNasc(request.getParameter("data_nasc"));
        usuario.setSexo( request.getParameter("sexo"));
        usuario.setLogradouro( request.getParameter("logradouro"));
        usuario.setNumero( Integer.valueOf(request.getParameter("numero")));
        usuario.setSetor( request.getParameter("setor"));
        usuario.setCidade( request.getParameter("cidade"));
        usuario.setUf( request.getParameter("uf"));
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        java.sql.Date date = null;
        try {
            date = new java.sql.Date(dateFormat.parse(usuario.getDataNasc()).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        en = Conexao.getEntityManager();
        DaoUsuario daoUsuario = new DaoUsuario(en);
        Usuario upUsuario = daoUsuario.findById(usuario.getId());
        if(upUsuario == null){
            response.sendRedirect("listar?ms='Agenda não encontrado'");
        }else {
            upUsuario.atualizarUsuario(usuario);
            daoUsuario.update(upUsuario);
            en.close();
        }
    }
    public void delete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long id = Long.parseLong(request.getParameter("id"));
        en = Conexao.getEntityManager();
        DaoUsuario daoUsuario = new DaoUsuario(en);
        Usuario usuario = daoUsuario.findById(id);
        if(usuario == null){
            response.sendRedirect("listar?ms='Usuário não encontrado'");
        }
        daoUsuario.delete(usuario);
        en.close();
        response.sendRedirect("listar");
    }

    private void getInsert(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        en = Conexao.getEntityManager();
        daoAlergia = new DaoAlergia(en);
        RequestDispatcher rd = request.getRequestDispatcher("/templates/usuario/cadastrar-usuario.jsp");
        request.setAttribute("alergias",daoAlergia.findAll());
        en.close();
        rd.forward(request, response);
    }

    private void getUpdate(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        long id =  Long.parseLong(request.getParameter("id"));
        en = Conexao.getEntityManager();
        DaoUsuario daoUsuario = new DaoUsuario(en);
        daoAlergia = new DaoAlergia(en);
        Usuario usuario = daoUsuario.findById(id);
        request.setAttribute("alergias",daoAlergia.findAll());
        RequestDispatcher rd = request.getRequestDispatcher("/templates/usuario/editar-usuario.jsp");
        request.setAttribute("usuario",usuario);
        en.close();
        rd.forward(request, response);
    }
    protected void getList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        en = Conexao.getEntityManager();
        DaoUsuario daoUsuario = new DaoUsuario(en);
        java.util.List<Usuario> usuarios = daoUsuario.findAll();
        en.close();
        RequestDispatcher rd = request.getRequestDispatcher("/templates/usuario/listar-usuarios.jsp");
        request.setAttribute("lista", usuarios);
        request.setAttribute("ms", request.getParameter("ms"));
        rd.forward(request, response);
    }
}