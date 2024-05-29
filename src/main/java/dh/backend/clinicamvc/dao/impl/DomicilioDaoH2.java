package dh.backend.clinicamvc.dao.impl;

import dh.backend.clinicamvc.dao.IDao;
import dh.backend.clinicamvc.db.H2Connection;
import dh.backend.clinicamvc.model.Domicilio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.List;

public class DomicilioDaoH2 implements IDao<Domicilio> {
    //Generamos el Logger importandola de log4j
    private static Logger LOGGER = LoggerFactory.getLogger(DomicilioDaoH2.class);
    //INSERTO MIS VALORES
    private static String SQL_INSERT = "INSERT INTO DOMICILIOS VALUES( DEFAULT, ? ,? ,?, ?)";

    //Para buscar por id
    private static String SQL_SELECT_ID = "SELECT * FROM DOMICILIOS WHERE ID = ?";



    @Override
    public Domicilio registrar(Domicilio domicilio) {
        //Hago mi connection en el primer método dejandola en null
        Connection connection = null;

        //Aqui mi domiciilio a retornar
        Domicilio domicilioARetornar = null;

        try{
            connection = H2Connection.getConnection();
            connection.setAutoCommit(false);

            //Aqui inyecto mis prepared statements que hice son SQL, y que me traiga las keys
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);

            //Aqui ingreso/Setteo  mis datos del domicilio con su tipo de dato y su getter
            preparedStatement.setString(1, domicilio.getCalle());
            preparedStatement.setInt(2, domicilio.getNumero());
            preparedStatement.setString(3,domicilio.getLocalidad());
            preparedStatement.setString(4,domicilio.getProvincia());

            //Luego executar con update porque estoy registrando
            preparedStatement.executeUpdate();

            // Para guardar las keys que me trae la base de datos H2, necesito un ResultSet
            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            //Traer la info del result set usando un while
            while(resultSet.next()){
                //le agrego la id e instancio un domicilio con todas las propiedades de docmicilio
                Integer id = resultSet.getInt(1);
                domicilioARetornar = new Domicilio(id, domicilio.getCalle(), domicilio.getNumero(), domicilio.getLocalidad(), domicilio.getProvincia());
            }
            LOGGER.info("Paciente persistido: " + domicilioARetornar);

            connection.commit();
            connection.setAutoCommit(true);

        }catch(Exception e){
            if(connection != null){
                try {
                    connection.rollback();
                } catch(SQLException ex){
                    LOGGER.info(ex.getMessage());
                    ex.printStackTrace();
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
        }
        //Retorno el domicilio a retornar
        return domicilioARetornar;
    }

    @Override
    public Domicilio buscarPorId(Integer id) {
        Connection connection = null;
        Domicilio domicilioEncontrado = null;

        try{
            //Me connecto como siempre
            connection = H2Connection.getConnection();
            //GENERO MI SQL SELECT by ID CON EL STATEMENT PREPARADO
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_ID);

            //Seteo mi id
            preparedStatement.setInt(1,id);

            // Lo ejecuto y nos trae un result set lo guardo ahi
            ResultSet resultSet = preparedStatement.executeQuery();

            //Ahora creo el domicilio dentro del while
            while(resultSet.next()){
                Integer idEncontrado = resultSet.getInt(1);
                String calle = resultSet.getNString(2);
                int numero = resultSet.getInt(3);
                String localidad = resultSet.getString(4);
                String provincia = resultSet.getString(5);

                domicilioEncontrado = new Domicilio(idEncontrado,calle, numero, localidad,provincia);
            }
            LOGGER.info("domicilio encontrado: " + domicilioEncontrado);


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

        return domicilioEncontrado;
    }

    @Override
    public List<Domicilio> buscarTodos() {
        return null;
    }
}
