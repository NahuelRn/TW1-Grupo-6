-- ==========================================
-- DATA: USUARIO 1 (jugador de prueba)
-- ==========================================
INSERT INTO Usuario (email, password, rol, activo)
VALUES ('jugador@unlam.edu.ar', '123456', 'USER', true);

-- Jugador asociado al Usuario 1
INSERT INTO Jugador (id, nivel, oro, usuario_id)
VALUES (1, 1, 0, 1);

-- ==========================================
-- DATA: CARTAS - ATAQUE
-- ==========================================
INSERT INTO Carta (nombre, tipo, subtipo, valorOroBase, nivelDesbloqueo, probabilidad, duracion, rareza, descripcion, danoMin, danoMax, defensaMin, defensaMax) VALUES
                                                                                                                                                                    ('Patada Voladora',      'ATAQUE', NULL,           50,   1, 100.00, 1, 'Comun',      'Daño: 4-8',    4,  8,  0, 0),
                                                                                                                                                                    ('Puntazo',              'ATAQUE', 'HEMORRAGIA',   150,  1, 70.00,  3, 'Poco Comun', 'Daño: 8-12',   8,  12, 0, 0),
                                                                                                                                                                    ('Disparo Rapido',       'ATAQUE', NULL,           150,  1, 100.00, 1, 'Poco Comun', 'Daño: 7-11',   7,  11, 0, 0),
                                                                                                                                                                    ('Tajo Basico',          'ATAQUE', NULL,           50,   1, 100.00, 1, 'Comun',      'Daño: 3-7',    3,  7,  0, 0),
                                                                                                                                                                    ('Golpe Pesado',         'ATAQUE', NULL,           150,  1, 100.00, 1, 'Poco Comun', 'Daño: 12-20',  12, 20, 0, 0),
                                                                                                                                                                    ('Corte Doble',          'ATAQUE', NULL,           250,  1, 100.00, 1, 'Rara',       'Daño: 6-10',   6,  10, 0, 0),
                                                                                                                                                                    ('Hachazo Brutal',       'ATAQUE', NULL,           150,  1, 100.00, 1, 'Poco Comun', 'Daño: 14-22',  14, 22, 0, 0),
                                                                                                                                                                    ('Tiro Preciso',         'ATAQUE', NULL,           250,  1, 100.00, 1, 'Rara',       'Daño: 5-8',    5,  8,  0, 0),
                                                                                                                                                                    ('Rompe Rodillas',       'ATAQUE', NULL,           150,  1, 100.00, 1, 'Poco Comun', 'Daño: 4-6',    4,  6,  0, 0),
                                                                                                                                                                    ('Estocada Venenosa',    'ATAQUE', 'VENENO',       250,  2, 100.00, 3, 'Rara',       'Daño: 3-5',    3,  5,  0, 0),
                                                                                                                                                                    ('Golpe Bajo',           'ATAQUE', 'ATURDIMIENTO', 150,  1, 50.00,  1, 'Poco Comun', 'Daño: 4-8',    4,  8,  0, 0),
                                                                                                                                                                    ('Frenesi Sangriento',   'ATAQUE', 'HEMORRAGIA',   500,  3, 100.00, 1, 'Exotica',    'Daño: 2-4',    2,  4,  0, 0),
                                                                                                                                                                    ('Tajo Cruzado',         'ATAQUE', 'HEMORRAGIA',   250,  2, 100.00, 1, 'Rara',       'Daño: 7-11',   7,  11, 0, 0),
                                                                                                                                                                    ('Machacar',             'ATAQUE', 'ESCUDO',       150,  1, 100.00, 1, 'Poco Comun', 'Daño: 8-12',   8,  12, 0, 0),
                                                                                                                                                                    ('Empujon',              'ATAQUE', NULL,           50,   1, 100.00, 1, 'Comun',      'Daño: 2-4',    2,  4,  0, 0),
                                                                                                                                                                    ('Guillotina',           'ATAQUE', NULL,           1000, 5, 100.00, 1, 'Legendaria', 'Daño: 20-30',  20, 30, 0, 0),
                                                                                                                                                                    ('Cuchillo Arrojadizo',  'ATAQUE', 'ROBO',         150,  1, 100.00, 1, 'Poco Comun', 'Daño: 2-4',    2,  4,  0, 0),
                                                                                                                                                                    ('Ataque Furtivo',       'ATAQUE', NULL,           250,  2, 100.00, 1, 'Rara',       'Daño: 10-14',  10, 14, 0, 0),
                                                                                                                                                                    ('Martillazo Sismico',   'ATAQUE', NULL,           500,  3, 100.00, 2, 'Exotica',    'Daño: 6-10',   6,  10, 0, 0),
                                                                                                                                                                    ('Embestida con Escudo', 'ATAQUE', 'ESCUDO',       250,  2, 100.00, 1, 'Rara',       'Daño: 4-6',    4,  6,  0, 0),
                                                                                                                                                                    ('Desgarrar',            'ATAQUE', 'HEMORRAGIA',   150,  1, 100.00, 1, 'Poco Comun', 'Daño: 5-7',    5,  7,  0, 0),
                                                                                                                                                                    ('Cuchillada Toxica',    'ATAQUE', 'VENENO',       500,  3, 100.00, 1, 'Exotica',    'Daño: 4-6',    4,  6,  0, 0),
                                                                                                                                                                    ('Golpe de Gracia',      'ATAQUE', 'ROBO',         250,  2, 100.00, 1, 'Rara',       'Daño: 10-14',  10, 14, 0, 0),
                                                                                                                                                                    ('Lanzamiento de Hacha', 'ATAQUE', NULL,           50,   1, 100.00, 1, 'Comun',      'Daño: 8-12',   8,  12, 0, 0),
                                                                                                                                                                    ('Ataque Oportunista',   'ATAQUE', NULL,           250,  2, 100.00, 1, 'Rara',       'Daño: 6-10',   6,  10, 0, 0),
                                                                                                                                                                    ('Danza de Espadas',     'ATAQUE', NULL,           500,  3, 100.00, 1, 'Exotica',    'Daño: 6-10',   6,  10, 0, 0),
                                                                                                                                                                    ('Golpe Espectral',      'ATAQUE', NULL,           500,  3, 100.00, 1, 'Exotica',    'Daño: 8-12',   8,  12, 0, 0),
                                                                                                                                                                    ('Granada Sagrada',      'ATAQUE', NULL,           1000, 6, 100.00, 1, 'Legendaria', 'Daño: 35-45',  35, 45, 0, 0),
                                                                                                                                                                    ('Vampirismo',           'ATAQUE', 'CURA',         250,  2, 100.00, 1, 'Rara',       'Daño: 6-10',   6,  10, 0, 0);

-- ==========================================
-- DATA: CARTAS - DEFENSA
-- ==========================================
INSERT INTO Carta (nombre, tipo, subtipo, valorOroBase, nivelDesbloqueo, probabilidad, duracion, rareza, descripcion, danoMin, danoMax, defensaMin, defensaMax) VALUES
                                                                                                                                                                    ('Escudo de Madera',     'DEFENSA', 'ESCUDO',       50,   1, 100.00, 1,  'Comun',      'Defensa: 3-5',   0, 0, 3,  5),
                                                                                                                                                                    ('Instinto',             'DEFENSA', NULL,            500,  3, 100.00, 1,  'Exotica',    'Defensa: 1-3',   0, 0, 1,  3),
                                                                                                                                                                    ('Piel de Hierro',       'DEFENSA', NULL,            250,  2, 80.00,  1,  'Rara',       'Defensa: 8-12',  0, 0, 8,  12),
                                                                                                                                                                    ('Bloqueo Perfecto',     'DEFENSA', 'ESCUDO',        150,  1, 100.00, 1,  'Poco Comun', 'Defensa: 10',    0, 0, 10, 10),
                                                                                                                                                                    ('Evasion Acrobatica',   'DEFENSA', 'ROBO',          250,  2, 100.00, 1,  'Rara',       'Defensa: 4-6',   0, 0, 4,  6),
                                                                                                                                                                    ('Postura Defensiva',    'DEFENSA', 'ESCUDO',        250,  2, 100.00, 3,  'Rara',       'Defensa: 12-18', 0, 0, 12, 18),
                                                                                                                                                                    ('Escudo de Pinchos',    'DEFENSA', 'ESCUDO',        150,  1, 100.00, 1,  'Poco Comun', 'Daño: 2-4 | Defensa: 3-5', 2, 4, 3, 5),
                                                                                                                                                                    ('Anticipacion',         'DEFENSA', 'ESCUDO',        150,  1, 100.00, 1,  'Poco Comun', 'Defensa: 4-6',   0, 0, 4,  6),
                                                                                                                                                                    ('Parada Defensiva',     'DEFENSA', 'ATURDIMIENTO',  250,  2, 100.00, 1,  'Rara',       'Defensa: 5-7',   0, 0, 5,  7),
                                                                                                                                                                    ('Muro de Piedra',       'DEFENSA', 'ESCUDO',        150,  1, 100.00, 1,  'Poco Comun', 'Defensa: 12-18', 0, 0, 12, 18),
                                                                                                                                                                    ('Reflejos Felinos',     'DEFENSA', NULL,            500,  3, 100.00, 3,  'Exotica',    'Defensa: 4-6',   0, 0, 4,  6),
                                                                                                                                                                    ('Ocultarse',            'DEFENSA', NULL,            500,  3, 100.00, 1,  'Exotica',    'Defensa: 4-6',   0, 0, 4,  6),
                                                                                                                                                                    ('Guardia Absoluta',     'DEFENSA', 'ESCUDO',        1000, 5, 100.00, 1,  'Legendaria', 'Defensa: 25-35', 0, 0, 25, 35),
                                                                                                                                                                    ('Sacrificio de Sangre', 'DEFENSA', 'ESCUDO',        250,  1, 100.00, 1,  'Rara',       'Defensa: 6-10',  0, 0, 6,  10),
                                                                                                                                                                    ('Absorcion de Impacto', 'DEFENSA', 'CURA',          500,  3, 50.00,  1,  'Exotica',    'Defensa: 4-6',   0, 0, 4,  6),
                                                                                                                                                                    ('Espejismo',            'DEFENSA', NULL,            250,  2, 20.00,  1,  'Rara',       'Defensa: 2-4',   0, 0, 2,  4),
                                                                                                                                                                    ('Escudo Torre',         'DEFENSA', 'ESCUDO',        500,  3, 100.00, 4,  'Exotica',    'Defensa: 6-10',  0, 0, 6,  10),
                                                                                                                                                                    ('Venganza Ciega',       'DEFENSA', 'ESCUDO',        150,  1, 100.00, 1,  'Poco Comun', 'Defensa: 3-5',   0, 0, 3,  5),
                                                                                                                                                                    ('Armadura de Espinas',  'DEFENSA', NULL,            1000, 4, 100.00, 99, 'Legendaria', 'Defensa: 4-6',   0, 0, 4,  6),
                                                                                                                                                                    ('Muro de Hielo',        'DEFENSA', 'ESCUDO',        250,  2, 100.00, 3,  'Rara',       'Defensa: 3-5',   0, 0, 3,  5);

-- ==========================================
-- DATA: CARTAS - HECHIZOS
-- ==========================================
INSERT INTO Carta (nombre, tipo, subtipo, valorOroBase, nivelDesbloqueo, probabilidad, duracion, rareza, descripcion, danoMin, danoMax, defensaMin, defensaMax) VALUES
                                                                                                                                                                    ('Pocion Curativa',       'HECHIZO', 'CURA',         50,   1, 100.00, 1, 'Comun',      'Efecto: +15 Vida',             0,  0,  15, 15),
                                                                                                                                                                    ('Bomba',                 'HECHIZO', NULL,            150,  1, 100.00, 1, 'Exotica',    'Daño: 12-18',                  12, 18, 0,  0),
                                                                                                                                                                    ('Vendaje',               'HECHIZO', 'CURA',         50,   1, 100.00, 1, 'Comun',      'Efecto: +5 Vida',              0,  0,  5,  5),
                                                                                                                                                                    ('Afilador de Armas',     'HECHIZO', NULL,            150,  1, 100.00, 1, 'Poco Comun', 'Efecto: +3 Daño',              0,  0,  0,  0),
                                                                                                                                                                    ('Adrenalina',            'HECHIZO', NULL,            250,  2, 100.00, 1, 'Rara',       'Efecto: Roba 4 cartas',        0,  0,  0,  0),
                                                                                                                                                                    ('Preparacion',           'HECHIZO', 'ROBO',         150,  1, 100.00, 1, 'Poco Comun', 'Efecto: Roba 2 cartas',        0,  0,  0,  0),
                                                                                                                                                                    ('Frasco de Acido',       'HECHIZO', NULL,            250,  2, 100.00, 1, 'Rara',       'Efecto: Rompe Armadura',       0,  0,  0,  0),
                                                                                                                                                                    ('Cortina de Humo',       'HECHIZO', NULL,            500,  3, 100.00, 2, 'Exotica',    'Efecto: Oculto',               0,  0,  0,  0),
                                                                                                                                                                    ('Pocion de Fuerza',      'HECHIZO', NULL,            250,  2, 100.00, 2, 'Rara',       'Efecto: +3 Daño',              0,  0,  0,  0),
                                                                                                                                                                    ('Antidoto',              'HECHIZO', 'CURA',         150,  1, 100.00, 3, 'Poco Comun', 'Efecto: Inmune a Veneno',      0,  0,  0,  0),
                                                                                                                                                                    ('Polvo Cegador',         'HECHIZO', NULL,            250,  2, 75.00,  1, 'Rara',       'Efecto: Ciega al Zombi',       0,  0,  0,  0),
                                                                                                                                                                    ('Concentracion',         'HECHIZO', 'ROBO',         500,  3, 100.00, 1, 'Exotica',    'Efecto: Elige carta',          0,  0,  0,  0),
                                                                                                                                                                    ('Brebaje Berserker',     'HECHIZO', NULL,            500,  3, 100.00, 3, 'Exotica',    'Efecto: +50% Daño',            0,  0,  0,  0),
                                                                                                                                                                    ('Trampa para Osos',      'HECHIZO', 'ATURDIMIENTO', 250,  2, 100.00, 1, 'Rara',       'Daño: 8-12',                   8,  12, 0,  0),
                                                                                                                                                                    ('Reciclar',              'HECHIZO', NULL,            1000, 4, 100.00, 1, 'Legendaria', 'Efecto: Descarta y Roba',      0,  0,  0,  0),
                                                                                                                                                                    ('Catalizador de Sangre', 'HECHIZO', 'VENENO',       500,  3, 100.00, 1, 'Exotica',    'Efecto: Convierte Hemorragia', 0,  0,  0,  0),
                                                                                                                                                                    ('Planificacion',         'HECHIZO', 'ROBO',         150,  1, 100.00, 1, 'Poco Comun', 'Efecto: Roba 1 carta',         0,  0,  0,  0),
                                                                                                                                                                    ('Ritual Oscuro',         'HECHIZO', 'ROBO',         250,  2, 100.00, 1, 'Rara',       'Efecto: Reinicia Mano',        0,  0,  0,  0),
                                                                                                                                                                    ('Piel de Obsidiana',     'HECHIZO', 'ESCUDO',       250,  2, 100.00, 2, 'Rara',       'Defensa: 10',                  0,  0,  10, 10),
                                                                                                                                                                    ('Contrato Demoniaco',    'HECHIZO', 'ROBO',         1000, 5, 100.00, 1, 'Legendaria', 'Efecto: -15 HP, Roba 5',       0,  0,  0,  0);

-- ==========================================
-- VINCULAR IMÁGENES
-- ==========================================
UPDATE Carta SET imagen = 'Patada_Voladora.jpg'      WHERE nombre = 'Patada Voladora';
UPDATE Carta SET imagen = 'Tajo_Basico.jpg'          WHERE nombre = 'Tajo Basico';
UPDATE Carta SET imagen = 'Puntazo.jpg'              WHERE nombre = 'Puntazo';
UPDATE Carta SET imagen = 'Golpe_Pesado.jpg'         WHERE nombre = 'Golpe Pesado';
UPDATE Carta SET imagen = 'Tajo_Cruzado.jpg'         WHERE nombre = 'Tajo Cruzado';
UPDATE Carta SET imagen = 'Cuchillo_Arrojadizo.jpg'  WHERE nombre = 'Cuchillo Arrojadizo';
UPDATE Carta SET imagen = 'Disparo_Rapido.jpg'       WHERE nombre = 'Disparo Rapido';
UPDATE Carta SET imagen = 'Corte_Doble.jpg'          WHERE nombre = 'Corte Doble';
UPDATE Carta SET imagen = 'Hachazo_Brutal.jpg'       WHERE nombre = 'Hachazo Brutal';
UPDATE Carta SET imagen = 'Tiro_Preciso.jpg'         WHERE nombre = 'Tiro Preciso';
UPDATE Carta SET imagen = 'Rompe_Rodillas.jpg'       WHERE nombre = 'Rompe Rodillas';
UPDATE Carta SET imagen = 'Estocada_Venenosa.jpg'    WHERE nombre = 'Estocada Venenosa';
UPDATE Carta SET imagen = 'Golpe_Bajo.jpg'           WHERE nombre = 'Golpe Bajo';
UPDATE Carta SET imagen = 'Frenesi_Sangriento.jpg'   WHERE nombre = 'Frenesi Sangriento';
UPDATE Carta SET imagen = 'Machacar.jpg'             WHERE nombre = 'Machacar';
UPDATE Carta SET imagen = 'Empujon.jpg'              WHERE nombre = 'Empujon';
UPDATE Carta SET imagen = 'Guillotina.jpg'           WHERE nombre = 'Guillotina';
UPDATE Carta SET imagen = 'Ataque_Furtivo.jpg'       WHERE nombre = 'Ataque Furtivo';
UPDATE Carta SET imagen = 'Martillazo_Sismico.jpg'   WHERE nombre = 'Martillazo Sismico';
UPDATE Carta SET imagen = 'Embestida_con_Escudo.jpg' WHERE nombre = 'Embestida con Escudo';
UPDATE Carta SET imagen = 'Desgarrar.jpg'            WHERE nombre = 'Desgarrar';
UPDATE Carta SET imagen = 'Cuchillada_Toxica.jpg'    WHERE nombre = 'Cuchillada Toxica';
UPDATE Carta SET imagen = 'Golpe_de_Gracia.jpg'      WHERE nombre = 'Golpe de Gracia';
UPDATE Carta SET imagen = 'Lanzamiento_de_Hacha.jpg' WHERE nombre = 'Lanzamiento de Hacha';
UPDATE Carta SET imagen = 'Ataque_Oportunista.jpg'   WHERE nombre = 'Ataque Oportunista';
UPDATE Carta SET imagen = 'Danza_de_Espadas.jpg'     WHERE nombre = 'Danza de Espadas';
UPDATE Carta SET imagen = 'Golpe_Espectral.jpg'      WHERE nombre = 'Golpe Espectral';
UPDATE Carta SET imagen = 'Granada_Sagrada.jpg'      WHERE nombre = 'Granada Sagrada';
UPDATE Carta SET imagen = 'Vampirismo.jpg'           WHERE nombre = 'Vampirismo';
UPDATE Carta SET imagen = 'Escudo_de_Madera.jpg'     WHERE nombre = 'Escudo de Madera';
UPDATE Carta SET imagen = 'Bloqueo_Perfecto.jpg'     WHERE nombre = 'Bloqueo Perfecto';
UPDATE Carta SET imagen = 'Postura_Defensiva.jpg'    WHERE nombre = 'Postura Defensiva';
UPDATE Carta SET imagen = 'Anticipacion.jpg'         WHERE nombre = 'Anticipacion';
UPDATE Carta SET imagen = 'Sacrificio_de_Sangre.jpg' WHERE nombre = 'Sacrificio de Sangre';
UPDATE Carta SET imagen = 'Muro_de_Piedra.jpg'       WHERE nombre = 'Muro de Piedra';
UPDATE Carta SET imagen = 'Instinto.jpg'             WHERE nombre = 'Instinto';
UPDATE Carta SET imagen = 'Piel_de_Hierro.jpg'       WHERE nombre = 'Piel de Hierro';
UPDATE Carta SET imagen = 'Evasion_Acrobatica.jpg'   WHERE nombre = 'Evasion Acrobatica';
UPDATE Carta SET imagen = 'Escudo_de_Pinchos.jpg'    WHERE nombre = 'Escudo de Pinchos';
UPDATE Carta SET imagen = 'Parada_Defensiva.jpg'     WHERE nombre = 'Parada Defensiva';
UPDATE Carta SET imagen = 'Reflejos_Felinos.jpg'     WHERE nombre = 'Reflejos Felinos';
UPDATE Carta SET imagen = 'Ocultarse.jpg'            WHERE nombre = 'Ocultarse';
UPDATE Carta SET imagen = 'Guardia_Absoluta.jpg'     WHERE nombre = 'Guardia Absoluta';
UPDATE Carta SET imagen = 'Absorcion_de_Impacto.jpg' WHERE nombre = 'Absorcion de Impacto';
UPDATE Carta SET imagen = 'Espejismo.jpg'            WHERE nombre = 'Espejismo';
UPDATE Carta SET imagen = 'Escudo_Torre.jpg'         WHERE nombre = 'Escudo Torre';
UPDATE Carta SET imagen = 'Venganza_Ciega.jpg'       WHERE nombre = 'Venganza Ciega';
UPDATE Carta SET imagen = 'Armadura_de_Espinas.jpg'  WHERE nombre = 'Armadura de Espinas';
UPDATE Carta SET imagen = 'Muro_de_Hielo.jpg'        WHERE nombre = 'Muro de Hielo';
UPDATE Carta SET imagen = 'Pocion_Curativa.jpg'      WHERE nombre = 'Pocion Curativa';
UPDATE Carta SET imagen = 'Preparacion.jpg'          WHERE nombre = 'Preparacion';
UPDATE Carta SET imagen = 'Afilado_de_Armas.jpg'     WHERE nombre = 'Afilador de Armas';
UPDATE Carta SET imagen = 'Bomba.jpg'                WHERE nombre = 'Bomba';
UPDATE Carta SET imagen = 'Vendaje.jpg'              WHERE nombre = 'Vendaje';
UPDATE Carta SET imagen = 'Adrenalina.jpg'           WHERE nombre = 'Adrenalina';
UPDATE Carta SET imagen = 'Frasco_de_Acido.jpg'      WHERE nombre = 'Frasco de Acido';
UPDATE Carta SET imagen = 'Cortina_de_Humo.jpg'      WHERE nombre = 'Cortina de Humo';
UPDATE Carta SET imagen = 'Pocion_de_Fuerza.jpg'     WHERE nombre = 'Pocion de Fuerza';
UPDATE Carta SET imagen = 'Antidoto.jpg'             WHERE nombre = 'Antidoto';
UPDATE Carta SET imagen = 'Polvo_Cegador.jpg'        WHERE nombre = 'Polvo Cegador';
UPDATE Carta SET imagen = 'Concentracion.jpg'        WHERE nombre = 'Concentracion';
UPDATE Carta SET imagen = 'Brebaje_Berserker.jpg'    WHERE nombre = 'Brebaje Berserker';
UPDATE Carta SET imagen = 'Trampa_para_Osos.jpg'     WHERE nombre = 'Trampa para Osos';
UPDATE Carta SET imagen = 'Reciclar.jpg'             WHERE nombre = 'Reciclar';
UPDATE Carta SET imagen = 'Catalizador_de_Sangre.jpg' WHERE nombre = 'Catalizador de Sangre';
UPDATE Carta SET imagen = 'Planificacion.jpg'        WHERE nombre = 'Planificacion';
UPDATE Carta SET imagen = 'Ritual_Oscuro.jpg'        WHERE nombre = 'Ritual Oscuro';
UPDATE Carta SET imagen = 'Piel_de_Obsidiana.jpg'    WHERE nombre = 'Piel de Obsidiana';
UPDATE Carta SET imagen = 'Contrato_Demoniaco.jpg'   WHERE nombre = 'Contrato Demoniaco';

-- ==========================================
-- DATA: ENEMIGOS
-- ==========================================
INSERT INTO Enemigo (nombre, hpBase, zona, danoMin, danoMax, imagen, probabilidad) VALUES
                                                                                       ('Infectado',        50,  'bosque',  3,  8,  'Infectado.jpg',          45),
                                                                                       ('Orco Furioso',     85,  'bosque',  8,  14, 'Orco.png',               40),
                                                                                       ('Cíclope',          110, 'bosque',  10, 18, 'Ciclope.png',            10),
                                                                                       ('Minotauro',        120, 'bosque',  10, 20, 'Minotauro.png',           5),
                                                                                       ('Araña de Cristal', 45,  'caverna', 5,  12, 'Araña_De_Cristal.png',  45),
                                                                                       ('Minero No-Muerto', 60,  'caverna', 4,  9,  'Minero_No-Muerto.png',  40),
                                                                                       ('Dragón',           80,  'caverna', 8,  16, 'Dragon.jpg',             10),
                                                                                       ('Dragón Ancestral', 150, 'caverna', 15, 25, 'Dragon_Ancestral.png',   5);

-- ==========================================
-- DATA: MAZO DEL JUGADOR 1
-- ==========================================
INSERT INTO Mazo (id) VALUES (1);
UPDATE Usuario SET mazo_activo_id = 1 WHERE email = 'jugador@unlam.edu.ar';

INSERT INTO MazoCarta (mazo_id, carta_id) VALUES
    (1, (SELECT id FROM Carta WHERE nombre = 'Tajo Basico'          LIMIT 1)),
(1, (SELECT id FROM Carta WHERE nombre = 'Patada Voladora'      LIMIT 1)),
(1, (SELECT id FROM Carta WHERE nombre = 'Disparo Rapido'       LIMIT 1)),
(1, (SELECT id FROM Carta WHERE nombre = 'Golpe Pesado'         LIMIT 1)),
(1, (SELECT id FROM Carta WHERE nombre = 'Cuchillo Arrojadizo'  LIMIT 1)),
(1, (SELECT id FROM Carta WHERE nombre = 'Escudo de Madera'     LIMIT 1)),
(1, (SELECT id FROM Carta WHERE nombre = 'Bloqueo Perfecto'     LIMIT 1)),
(1, (SELECT id FROM Carta WHERE nombre = 'Evasion Acrobatica'   LIMIT 1)),
(1, (SELECT id FROM Carta WHERE nombre = 'Pocion Curativa'      LIMIT 1)),
(1, (SELECT id FROM Carta WHERE nombre = 'Vendaje'              LIMIT 1)),
(1, (SELECT id FROM Carta WHERE nombre = 'Adrenalina'           LIMIT 1)),
(1, (SELECT id FROM Carta WHERE nombre = 'Tiro Preciso'         LIMIT 1)),
(1, (SELECT id FROM Carta WHERE nombre = 'Frenesi Sangriento'   LIMIT 1)),
(1, (SELECT id FROM Carta WHERE nombre = 'Piel de Hierro'       LIMIT 1)),
(1, (SELECT id FROM Carta WHERE nombre = 'Bomba'                LIMIT 1));

-- ==========================================
-- DATA: INVENTARIO DEL JUGADOR 1 (15 cartas x1)
-- ==========================================
INSERT INTO ItemInventario (cantidad, carta_id, jugador_id) VALUES
                                                                (1, (SELECT id FROM Carta WHERE nombre = 'Tajo Basico'          LIMIT 1), 1),
(1, (SELECT id FROM Carta WHERE nombre = 'Patada Voladora'      LIMIT 1), 1),
(1, (SELECT id FROM Carta WHERE nombre = 'Disparo Rapido'       LIMIT 1), 1),
(1, (SELECT id FROM Carta WHERE nombre = 'Golpe Pesado'         LIMIT 1), 1),
(1, (SELECT id FROM Carta WHERE nombre = 'Cuchillo Arrojadizo'  LIMIT 1), 1),
(1, (SELECT id FROM Carta WHERE nombre = 'Escudo de Madera'     LIMIT 1), 1),
(1, (SELECT id FROM Carta WHERE nombre = 'Bloqueo Perfecto'     LIMIT 1), 1),
(1, (SELECT id FROM Carta WHERE nombre = 'Evasion Acrobatica'   LIMIT 1), 1),
(1, (SELECT id FROM Carta WHERE nombre = 'Pocion Curativa'      LIMIT 1), 1),
(1, (SELECT id FROM Carta WHERE nombre = 'Vendaje'              LIMIT 1), 1),
(1, (SELECT id FROM Carta WHERE nombre = 'Adrenalina'           LIMIT 1), 1),
(1, (SELECT id FROM Carta WHERE nombre = 'Tiro Preciso'         LIMIT 1), 1),
(1, (SELECT id FROM Carta WHERE nombre = 'Frenesi Sangriento'   LIMIT 1), 1),
(1, (SELECT id FROM Carta WHERE nombre = 'Piel de Hierro'       LIMIT 1), 1),
(1, (SELECT id FROM Carta WHERE nombre = 'Bomba'                LIMIT 1), 1);

-- ==========================================
-- DATA: USUARIO 2 (coleccionista - TODAS las cartas x5)
-- ==========================================
INSERT INTO Usuario (email, password, rol, activo)
VALUES ('coleccionista@unlam.edu.ar', '123456', 'USER', true);

INSERT INTO Jugador (nivel, oro, usuario_id)
VALUES (1, 0, LAST_INSERT_ID());

SET @jugador_coleccionista = LAST_INSERT_ID();

-- Una fila por cada carta existente, cantidad = 5
INSERT INTO ItemInventario (cantidad, carta_id, jugador_id)
SELECT 5, id, @jugador_coleccionista
FROM Carta;