package org.example.ibb_ecodation_javafx.common;

import org.example.ibb_ecodation_javafx.common.interfaces.Creatable;
import org.example.ibb_ecodation_javafx.common.interfaces.Deletable;
import org.example.ibb_ecodation_javafx.common.interfaces.Readable;
import org.example.ibb_ecodation_javafx.common.interfaces.Updatable;

public interface CrudOperation<Entity> extends Creatable<Entity>, Readable<Entity>, Updatable<Entity>, Deletable<Entity> {

}
