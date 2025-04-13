package org.example.ibb_ecodation_javafx.repository;

import org.example.ibb_ecodation_javafx.core.db.MsSqlConnection;
import org.example.ibb_ecodation_javafx.model.Vat;
import org.example.ibb_ecodation_javafx.repository.base.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("vatRepository")
public class VatRepository extends BaseRepository<Vat> {

    @Autowired
    public VatRepository(MsSqlConnection msSqlConnection) {
        super(msSqlConnection.connectToDatabase());
    }
}