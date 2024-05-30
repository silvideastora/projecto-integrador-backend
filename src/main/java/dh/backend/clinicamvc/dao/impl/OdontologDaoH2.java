package dh.backend.clinicamvc.dao.impl;

import dh.backend.clinicamvc.dao.IDao;
import dh.backend.clinicamvc.db.H2Connection;
import dh.backend.clinicamvc.model.Odontologo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class OdontologDaoH2 implements IDao<Odontologo> {
    //Crea el logger factory
    private static Logger LOGGER = LoggerFactory.getLogger(OdontologDaoH2.class);

    //Inserto mi SQL en mi prepared Statement
    private static String SQL_INSERT = "INSERT INTO ODONTOLOGOS VALUES (DEFAULT, ?, ?, ?)";

    //Inserto mi SQL para buscar por ID en mi prepared Statement
    private static String SQL_SELECT_ID = "SELECT * FROM ODONTOLOGOS WHERE ID = ?";

    //Inserto mi SQL para traernos a todos los pacientes en la lista
    private static String SQL_SELECT_ALL = "SELECT * FROM ODONTOLOGOS";




    @Override
    public Odontologo registrar(Odontologo odontologo) {
        return null;
    }

    @Override
    public Odontologo buscarPorId(Integer id) {
        //Hago mi connection
        Connection connection = null;
        //Creo mi odontologo
        Odontologo odontologoEncontrado = null;

        //Connection con base de datos
        try{
            connection = H2Connection.getConnection();
            //Preparo el statement para buscar por id
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_ID);
            preparedStatement.setInt(1, id);

            //El query Lo guardo en un result set y lo ejecuto
            ResultSet resultSet = preparedStatement.executeQuery();

            //Ciclo while
            while(resultSet.next()){
                Integer idDevuelto = resultSet.getInt(1);
                String apellido = resultSet.getString(2);
                String nombre = resultSet.getString(3);
                String matricula = resultSet.getString(4);

                //Armo mi odontologo completo
                odontologoEncontrado = new Odontologo(idDevuelto,apellido,nombre,matricula);

            }
            LOGGER.info("Odontologo encontrado: "+ odontologoEncontrado);


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

        LOGGER.info("Odontologo con matricula: "+ odontologoEncontrado.getMatricula());
        return odontologoEncontrado;
    }

    @Override
    public List<Odontologo> buscarTodos() {
        return List.of();
    }
}
