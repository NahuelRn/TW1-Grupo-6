-- ==========================================
-- DATA: USUARIO DE PRUEBA
-- ==========================================
INSERT INTO Usuario (email, password, rol, activo)
VALUES ('jugador@unlam.edu.ar', '123456', 'USER', true);

-- ==========================================
-- DATA: CARTAS - ATAQUE
-- ==========================================
INSERT INTO Carta (nombre, tipo, subtipo, valorOroBase, nivelDesbloqueo, probabilidad, duracion, rareza, descripcion) VALUES
                                                                                                                          ('Patada Voladora',      'ATAQUE', NULL,           50,  1, 100.00, 1, 'Comun', '5 - 7 de Daño.'),
                                                                                                                          ('Puntazo',            'ATAQUE', 'HEMORRAGIA',   150, 1, 70.00,  3, 'Poco Comun', '9 - 12 de Daño. 70% de probabilidad de aplicar Hemorragia durante 2 turnos al objetivo.'),
                                                                                                                          ('Disparo Rapido',       'ATAQUE', NULL,           150, 1, 100.00, 1, 'Poco Comun', '8 - 10 de Daño. +50% de Daño a enemigos con Defensa.'),
                                                                                                                          ('Tajo Basico',          'ATAQUE', NULL,           50,  1, 100.00, 1, 'Comun', '4 - 6 de Daño.'),
                                                                                                                          ('Golpe Pesado',         'ATAQUE', NULL,           150, 1, 100.00, 1, 'Poco Comun', '14 - 18 de Daño.'),
                                                                                                                          ('Corte Doble',          'ATAQUE', NULL,           250, 1, 100.00, 1, 'Rara', '4 - 5 de Daño. Ataca 2 veces al objetivo.'),
                                                                                                                          ('Hachazo Brutal',       'ATAQUE', NULL,           150, 1, 100.00, 1, 'Poco Comun', '16 - 20 de Daño.'),
                                                                                                                          ('Tiro Preciso',         'ATAQUE', NULL,           250, 1, 100.00, 1, 'Rara', '6 de Daño. Ignora el 50% de Defensa del objetivo.'),
                                                                                                                          ('Rompe Rodillas',       'ATAQUE', NULL,           150, 1, 100.00, 1, 'Poco Comun', '5 de Daño. Reduce Defensa del objetivo a 0.'),
                                                                                                                          ('Estocada Venenosa',    'ATAQUE', 'VENENO',       250, 2, 100.00, 3, 'Rara', '4 de Daño. Aplica Veneno por 3 turnos al objetivo.'),
                                                                                                                          ('Golpe Bajo',           'ATAQUE', 'ATURDIMIENTO', 150, 1, 50.00,  1, 'Poco Comun', '6 de Daño. 50% de probabilidad de Aturdir 1 turno al objetivo.'),
                                                                                                                          ('Frenesi Sangriento',   'ATAQUE', 'HEMORRAGIA',   500, 3, 100.00, 1, 'Exotica', '3 de Daño. Repite el efecto igual al número de Hemorragias que tenga el objetivo.'),
                                                                                                                          ('Tajo Cruzado',         'ATAQUE', 'HEMORRAGIA',   250, 2, 100.00, 1, 'Rara', '8 - 10 de Daño. Aplica Hemorragia al objetivo.'),
                                                                                                                          ('Machacar',             'ATAQUE', 'ESCUDO',       150, 1, 100.00, 1, 'Poco Comun', '10 de Daño. +5 de Ataque si el objetivo tiene Escudo.'),
                                                                                                                          ('Empujon',              'ATAQUE', NULL,           50,  1, 100.00, 1, 'Comun', '3 de Daño. Obliga al objetivo a defenderse.'),
                                                                                                                          ('Guillotina',           'ATAQUE', NULL,           1000,5, 100.00, 1, 'Legendaria', '25 de Daño. Usable si el objetivo tiene menos del 30% de vida.'),
                                                                                                                          ('Cuchillo Arrojadizo',  'ATAQUE', 'ROBO',         150, 1, 100.00, 1, 'Poco Comun', '3 de Daño. Roba 1 carta.'),
                                                                                                                          ('Ataque Furtivo',       'ATAQUE', NULL,           250, 2, 100.00, 1, 'Rara', '12 de Daño. Solo jugable como primera carta de ataque o si se esta [Oculto].'),
                                                                                                                          ('Martillazo Sismico',   'ATAQUE', NULL,           500, 3, 100.00, 2, 'Exotica', '8 de Daño. Aplica Debilidad por 2 turnos.'),
                                                                                                                          ('Embestida con Escudo', 'ATAQUE', 'ESCUDO',       250, 2, 100.00, 1, 'Rara', 'Inflige Daño igual a tu cantidad de Bloqueo.'),
                                                                                                                          ('Desgarrar',            'ATAQUE', 'HEMORRAGIA',   150, 1, 100.00, 1, 'Poco Comun', '6 de Daño. Aplica Hemorragia.'),
                                                                                                                          ('Cuchillada Toxica',    'ATAQUE', 'VENENO',       500, 3, 100.00, 1, 'Exotica', '5 de Daño. Duplica el Veneno del enemigo.'),
                                                                                                                          ('Golpe de Gracia',      'ATAQUE', 'ROBO',         250, 2, 100.00, 1, 'Rara', '12 de Daño. Si mata al objetivo, roba 2 cartas.'),
                                                                                                                          ('Lanzamiento de Hacha', 'ATAQUE', NULL,           50,  1, 100.00, 1, 'Comun', '10 de Daño. Descartas 1 carta de tu mano.'),
                                                                                                                          ('Ataque Oportunista',   'ATAQUE', NULL,           250, 2, 100.00, 1, 'Rara', '8 de Daño. No se consume si esquivaste antes.'),
                                                                                                                          ('Danza de Espadas',     'ATAQUE', NULL,           500, 3, 100.00, 1, 'Exotica', '2 de Daño. Ataca 4 veces.'),
                                                                                                                          ('Golpe Espectral',      'ATAQUE', NULL,           500, 3, 100.00, 1, 'Exotica', '10 de Daño. Atraviesa la defensa del objetivo.'),
                                                                                                                          ('Granada Sagrada',    'ATAQUE', NULL,           1000,6, 100.00, 1, 'Legendaria', '40 de Daño.'),
                                                                                                                          ('Vampirismo',           'ATAQUE', 'CURA',         250, 2, 100.00, 1, 'Rara', '5 - 10 de Daño. Te curas la cantidad de daño infligido.');

-- ==========================================
-- DATA: CARTAS - DEFENSA
-- ==========================================
INSERT INTO Carta (nombre, tipo, subtipo, valorOroBase, nivelDesbloqueo, probabilidad, duracion, rareza, descripcion) VALUES
                                                                                                                          ('Escudo de Madera',     'DEFENSA', 'ESCUDO',  50,  1, 100.00, 1, 'Comun', 'Bloquea 2 - 5 de Daño.'),
                                                                                                                          ('Instinto',             'DEFENSA', NULL,      500, 3, 100.00, 1, 'Exotica', '+10% de probabilidad de Esquivar por carta de DEFENSA en la mano.'),
                                                                                                                          ('Piel de Hierro',       'DEFENSA', NULL,      250, 2, 80.00,  1, 'Rara', 'Sacrificas turno. +80% de Defensa durante 3 turnos.'),
                                                                                                                          ('Bloqueo Perfecto',     'DEFENSA', 'ESCUDO',  150, 1, 100.00, 1, 'Poco Comun', 'Bloquea 10 de Daño.'),
                                                                                                                          ('Evasion Acrobatica',   'DEFENSA', 'ROBO',    250, 2, 100.00, 1, 'Rara', '100% de probabilidad de Esquivar el proximo ataque. Roba 1 carta.'),
                                                                                                                          ('Postura Defensiva',    'DEFENSA', 'ESCUDO',  250, 2, 100.00, 3, 'Rara', 'Ganas 5 Bloqueos [Bloquea 3 de daño cada uno] por 3 turnos.'),
                                                                                                                          ('Escudo de Pinchos',    'DEFENSA', 'ESCUDO',  150, 1, 100.00, 1, 'Poco Comun', 'Bloquea 4 Daño. Devuelve 3 Daño.'),
                                                                                                                          ('Anticipacion',         'DEFENSA', 'ESCUDO',  150, 1, 100.00, 1, 'Poco Comun', 'Si el oponente utiliza una carta de ATAQUE, bloquea 5 Daño.'),
                                                                                                                          ('Parada Defensiva',     'DEFENSA', 'ATURDIMIENTO', 250, 2, 100.00, 1, 'Rara', 'Bloquea 6 Daño. Si el ataque inflige menos o igual a 6 de daño, aturde durante 1 turno.'),
                                                                                                                          ('Muro de Piedra',       'DEFENSA', 'ESCUDO',  150, 1, 100.00, 1, 'Poco Comun', 'Bloquea 15 Daño.'),
                                                                                                                          ('Reflejos Felinos',     'DEFENSA', NULL,      500, 3, 100.00, 3, 'Exotica', 'Esquiva los proximos 3 ataques.'),
                                                                                                                          ('Ocultarse',            'DEFENSA', NULL,      500, 3, 100.00, 1, 'Exotica', 'El objetivo falla su proximo ataque.'),
                                                                                                                          ('Guardia Absoluta',     'DEFENSA', 'ESCUDO',  1000,5, 100.00, 1, 'Legendaria', 'Bloquea TODO el daño recibido durante 5 turnos.'),
                                                                                                                          ('Sacrificio de Sangre', 'DEFENSA', 'ESCUDO',  250, 1, 100.00, 1, 'Rara', 'Recibes 2 de Daño. Ganas 8 de Bloqueo.'),
                                                                                                                          ('Absorcion de Impacto', 'DEFENSA', 'CURA',    500, 3, 50.00,  1, 'Exotica', 'Bloquea 5 Daño. Te curas 50% del daño recibido.'),
                                                                                                                          ('Espejismo',            'DEFENSA', NULL,      250, 2, 20.00,  1, 'Rara', '20% de probabilidad de Esquivar el ataque enemigo.'),
                                                                                                                          ('Escudo Torre',         'DEFENSA', 'ESCUDO',  500, 3, 100.00, 4, 'Exotica', '8 de Bloqueo. Pierde 2 de Bloqueo por turno.'),
                                                                                                                          ('Venganza Ciega',       'DEFENSA', 'ESCUDO',  150, 1, 100.00, 1, 'Poco Comun', '4 de Bloqueo. Ganas Ataque equivalente al daño recibido.'),
                                                                                                                          ('Armadura de Espinas',  'DEFENSA', NULL,      1000,4, 100.00, 99,'Legendaria', '5 de Bloqueo. Atacantes reciben 3 Daño. Dura 4 turnos.'),
                                                                                                                          ('Muro de Hielo',        'DEFENSA', 'ESCUDO',  250, 2, 100.00, 3, 'Rara', '4 de Bloqueo. Atacantes reciben -2 de Ataque durante 3 turnos.');

-- ==========================================
-- DATA: CARTAS - HECHIZOS
-- ==========================================
INSERT INTO Carta (nombre, tipo, subtipo, valorOroBase, nivelDesbloqueo, probabilidad, duracion, rareza, descripcion) VALUES
                                                                                                                          ('Pocion Curativa',           'HECHIZO', 'CURA',         50,  1, 100.00, 1,  'Comun', '+15 de Vida. 3 usos.'),
                                                                                                                          ('Bomba',                     'HECHIZO', NULL,           150, 1, 100.00, 1,  'Exotica', '15 Daño. Elimina Bloqueos, defensas y esquives, aturde durante 1 turno. 1 uso.'),
                                                                                                                          ('Vendaje',                   'HECHIZO', 'CURA',         50,  1, 100.00, 1,  'Comun', '+5 Vida. Elimina Hemorragia. 2 usos.'),
                                                                                                                          ('Afilador de Armas',         'HECHIZO', NULL,           150, 1, 100.00, 1,  'Poco Comun', '+3 Daño a las siguientes 2 Cartas de Ataque. 2 uso.'),
                                                                                                                          ('Adrenalina',                'HECHIZO', NULL,           250, 2, 100.00, 1,  'Rara', 'Roba 4 cartas. 1 uso.'),
                                                                                                                          ('Preparacion',               'HECHIZO', 'ROBO',         150, 1, 100.00, 1,  'Poco Comun', 'Roba 2 cartas. 1 uso'),
                                                                                                                          ('Frasco de Acido',           'HECHIZO', NULL,           250, 2, 100.00, 1,  'Rara', 'Reduce la armadura del objetivo a 0. 2 usos.'),
                                                                                                                          ('Cortina de Humo',           'HECHIZO', NULL,           500, 3, 100.00, 2,  'Exotica', 'Te da [Oculto] durante 2 turnos. 2 usos.'),
                                                                                                                          ('Pocion de Fuerza',          'HECHIZO', NULL,           250, 2, 100.00, 2,  'Rara', '+3 de Daño por 2 turnos. 2 usos.'),
                                                                                                                          ('Antidoto',                  'HECHIZO', 'CURA',         150, 1, 100.00, 3,  'Poco Comun', 'Elimina e inmuniza al Veneno. Dura 4 turnos.'),
                                                                                                                          ('Polvo Cegador',             'HECHIZO', NULL,           250, 2, 75.00,  1,  'Rara', 'Ciega al objetivo por 1 turno, haciendo que su ataque falle. 2 usos.'),
                                                                                                                          ('Concentracion',             'HECHIZO', 'ROBO',         500, 3, 100.00, 1,  'Exotica', 'Eliges que carta robar. 1 uso.'),
                                                                                                                          ('Brebaje Berserker',         'HECHIZO', NULL,           500, 3, 100.00, 3,  'Exotica', '+50% de Daño, +50% de Daño recibido. Dura hasta finalizar el combate. 1 uso.'),
                                                                                                                          ('Trampa para Osos',          'HECHIZO', 'ATURDIMIENTO', 250, 2, 100.00, 1,  'Rara', 'Enemigo recibe 10 Daño y pierde su turno. 1 uso.'),
                                                                                                                          ('Reciclar',                  'HECHIZO', NULL,           1000,4, 100.00, 1,  'Legendaria', 'Descarta hasta 3 cartas y lvanta del mazo la misma cantidad. 2 usos.'),
                                                                                                                          ('Catalizador de Sangre',     'HECHIZO', 'VENENO',       500, 3, 100.00, 1,  'Exotica', 'Convierte Hemorragia en Veneno y lo duplica. 2 usos.'),
                                                                                                                          ('Planificacion',             'HECHIZO', 'ROBO',         150, 1, 100.00, 1,  'Poco Comun', 'Roba 1 carta. 1 uso.'),
                                                                                                                          ('Ritual Oscuro',             'HECHIZO', 'ROBO',         250, 2, 100.00, 1,  'Rara', 'Descarta tu mano entera. Roba misma cantidad. 1 uso.'),
                                                                                                                          ('Piel de Obsidiana',         'HECHIZO', 'ESCUDO',       250, 2, 100.00, 2,  'Rara', 'Escudo que bloquea 10 de Daño que al agotarse da [Oculto] durante 1 turno. 1 uso.'),
                                                                                                                          ('Contrato Demoniaco',        'HECHIZO', 'ROBO',         1000,5, 100.00, 1,  'Legendaria', 'Roba 5 cartas, -15 HP.');

-- ==========================================
-- VINCULAR IMÁGENES A LAS 15 CARTAS INICIALES
-- ==========================================
UPDATE Carta SET imagen = 'Patada_Voladora.jpg' WHERE nombre = 'Patada Voladora';
UPDATE Carta SET imagen = 'Tajo_Basico.jpg' WHERE nombre = 'Tajo Basico';
UPDATE Carta SET imagen = 'Puntazo.jpg' WHERE nombre = 'Puntazo';
UPDATE Carta SET imagen = 'Golpe_Pesado.jpg' WHERE nombre = 'Golpe Pesado';
UPDATE Carta SET imagen = 'Tajo_Cruzado.jpg' WHERE nombre = 'Tajo Cruzado';
UPDATE Carta SET imagen = 'Cuchillo_Arrojadizo.jpg' WHERE nombre = 'Cuchillo Arrojadizo';

UPDATE Carta SET imagen = 'Escudo_de_Madera.jpg' WHERE nombre = 'Escudo de Madera';
UPDATE Carta SET imagen = 'Bloqueo_Perfecto.jpg' WHERE nombre = 'Bloqueo Perfecto';
UPDATE Carta SET imagen = 'Postura_Defensiva.jpg' WHERE nombre = 'Postura Defensiva';
UPDATE Carta SET imagen = 'Anticipacion.jpg' WHERE nombre = 'Anticipacion';
UPDATE Carta SET imagen = 'Sacrificio_de_Sangre.jpg' WHERE nombre = 'Sacrificio de Sangre';
UPDATE Carta SET imagen = 'Muro_de_Piedra.jpg' WHERE nombre = 'Muro de Piedra';

UPDATE Carta SET imagen = 'Pocion_Curativa.jpg' WHERE nombre = 'Pocion Curativa';
UPDATE Carta SET imagen = 'Preparacion.png' WHERE nombre = 'Preparacion'; -- Era .png y decía Areparacion
UPDATE Carta SET imagen = 'Afilado_de_Armas.png' WHERE nombre = 'Afilador de Armas'; -- Era .png