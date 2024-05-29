package dh.backend.clinicamvc.dao.impl;

import dh.backend.clinicamvc.dao.IDao;
import dh.backend.clinicamvc.db.H2Connection;
import dh.backend.clinicamvc.model.Domicilio;
import dh.backend.clinicamvc.model.Paciente;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PacienteDaoH2 implements IDao<Paciente> {
    //Siempre se implementan sus metodos de la Dao
    //Hago mi Logger
    private static Logger LOGGER = LoggerFactory.getLogger(PacienteDaoH2.class);

    //Inserto mi SQL en mi prepared Statement
    private static String SQL_INSERT = "INSERT INTO PACIENTES VALUES(DEFAULT, ?, ?, ?, ?, ?)";

    //Inserto mi SQL para buscar por ID en mi prepared Statement
    private static String SQL_SELECT_ID = "SELECT * FROM PACIENTES WHERE ID = ?";

    //Inserto mi SQL para traernos a todos los pacientes en la lista
    private static String SQL_SELECT_ALL = "SELECT * FROM PACIENTES";




    @Override
    public Paciente registrar(Paciente paciente) {
        //  Me conecto
        Connection connection = null;
        // Invoco a DomicilioDaoH2 e instancio uno nuevo para conocer el domicilio
        DomicilioDaoH2 domicilioDaoH2 = new DomicilioDaoH2();
        // Creo donde voy a guardar a mi paciente a retornar
        Paciente pacienteARetornar = null;

        try {
            connection = H2Connection.getConnection();
            connection.setAutoCommit(false);

            //Llamo a domicilioH2 para importar su metodo de registro y pasarle al paciente con getDomicilio, ahi vine el Id
            Domicilio domicilioGuardado = domicilioDaoH2.registrar(paciente.getDomicilio());


            PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            //Ya que lo haya inyectado, Lo lleno con todos los signos ?
            preparedStatement.setString(1, paciente.getApellido());
            preparedStatement.setString(2,paciente.getNombre());
            preparedStatement.setString(3, paciente.getDni());
            preparedStatement.setDate(4, Date.valueOf(paciente.getFechaIngreso()));
            preparedStatement.setInt(5,domicilioGuardado.getId());

            //Luego executar con update porque estoy registrando
            preparedStatement.executeUpdate();

            // Para guardar las keys que me trae la base de datos H2, necesito un ResultSet
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
             //Guardo en un while
            while(resultSet.next()){
                //Obtenemos los datos del resultset
                Integer id = resultSet.getInt(1);


                pacienteARetornar = new Paciente(id,paciente.getApellido(),paciente.getNombre(), paciente.getDni(), paciente.getFechaIngreso(),domicilioGuardado);

            }
            LOGGER.info("Paciente guardado" + pacienteARetornar);

            connection.commit();
            connection.setAutoCommit(true);

        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    LOGGER.info(e.getMessage());
                    e.printStackTrace();
                }
            }
            LOGGER.info(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                LOGGER.info(e.getMessage());
                e.printStackTrace();
            }

            return pacienteARetornar;
        }
    }

    @Override
    public Paciente buscarPorId(Integer id) {
        Connection connection = null;
        Paciente pacienteEncontrado = null;
        DomicilioDaoH2 domicilioDaoH2 = new DomicilioDaoH2();


        try {
            connection = H2Connection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_ID);
            preparedStatement.setInt(1,id);

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                // Me trae cosas del result set para eso debo hacer un domicilio
                Integer idDevuelto = resultSet.getInt(1);
                String apellido = resultSet.getString(2);
                String nombre = resultSet.getString(3);
                String dni = resultSet.getString(4);
                LocalDate fechaIngreso = resultSet.getDate(5).toLocalDate();
                Integer idDomicilio = resultSet.getInt(6);
                Domicilio domicilioEncontrado = domicilioDaoH2.buscarPorId(idDomicilio);

                //Armo mi paciente completo
                pacienteEncontrado = new Paciente(idDevuelto,apellido,nombre,dni,fechaIngreso,domicilioEncontrado);
            }
            LOGGER.info("Paciente encontrado" + pacienteEncontrado);


        } catch(Exception e){
            LOGGER.info(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                LOGGER.info(e.getMessage());
                e.printStackTrace();
            }
        }


        return pacienteEncontrado;
    }

    @Override
    public List<Paciente> buscarTodos() {
        List<Paciente> pacientes = new ArrayList<>();

        Connection connection = null;
        DomicilioDaoH2 domicilioDaoH2 = new DomicilioDaoH2();

        try {

            connection = H2Connection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SQL_SELECT_ALL);

            while(resultSet.next()){
                //Cada vez que pase el while armo un paciente

                Integer idDevuelto = resultSet.getInt(1);
                String apellido = resultSet.getString(2);
                String nombre = resultSet.getString(3);
                String dni = resultSet.getString(4);
                LocalDate fechaIngreso = resultSet.getDate(5).toLocalDate();
                Integer idDomicilio = resultSet.getInt(6);
                Domicilio domicilioEncontrado = domicilioDaoH2.buscarPorId(idDomicilio);
                Paciente paciente = new Paciente(idDevuelto,apellido,nombre,dni,fechaIngreso,domicilioEncontrado);


                LOGGER.info("Pacientes listado: " + pacientes);
                //Lo guardo en la lista
                pacientes.add(paciente);
            }

        } catch(Exception e){
            LOGGER.info(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                LOGGER.info(e.getMessage());
                e.printStackTrace();
            }
        }

        return pacientes;
    }
}


