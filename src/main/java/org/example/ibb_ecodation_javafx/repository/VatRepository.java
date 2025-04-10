package org.example.ibb_ecodation_javafx.repository;

import org.example.ibb_ecodation_javafx.core.db.MsSqlConnection;
import org.example.ibb_ecodation_javafx.model.Vat;
import org.example.ibb_ecodation_javafx.repository.base.BaseRepository;
import org.springframework.stereotype.Component;
import java.sql.Connection;

@Component("vatRepository")
public class VatRepository extends BaseRepository<Vat> {
    public VatRepository() {
        super(MsSqlConnection.getInstance().connectToDatabase());
    }
}
