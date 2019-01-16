package com.poll.entity.ext;

import com.poll.entity.ProductBrandEntity;
import com.poll.entity.ProductEntity;
import com.poll.entity.ProductTypeEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ProductEntityExt extends ProductEntity {

    private static final long serialVersionUID = -3695780758498311381L;

    private ProductTypeEntity type;
    private ProductBrandEntity brand;
}
