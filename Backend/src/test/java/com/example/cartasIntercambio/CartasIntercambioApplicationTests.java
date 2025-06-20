package com.example.cartasIntercambio;

import com.example.cartasIntercambio.service.PublicacionServiceImplTest;
import com.example.cartasIntercambio.service.UsuarioServiceImplTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {PublicacionServiceImplTest.class, UsuarioServiceImplTest.class})
class CartasIntercambioApplicationTests {


	@Test
	void contextLoads() {
	}

}
