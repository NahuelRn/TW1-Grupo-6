-- ==========================================
-- DATA: USUARIO DE PRUEBA
-- ==========================================
INSERT INTO Usuario (email, password, rol, activo)
VALUES ('jugador@unlam.edu.ar', '123456', 'USER', true);

-- ==========================================
-- DATA: CARTAS - ATAQUE
-- ==========================================
INSERT INTO Carta (nombre, tipo, subtipo, valorOroBase, nivelDesbloqueo, probabilidad, duracion, rareza, descripcion, dano, defensa) VALUES
                                                                                                                                         ('Patada Voladora',      'ATAQUE', NULL,           50,  1, 100.00, 1, 'Comun',      'Daño: 6 | Defensa: 0', 6, 0),
                                                                                                                                         ('Puntazo',              'ATAQUE', 'HEMORRAGIA',   150, 1, 70.00,  3, 'Poco Comun', 'Daño: 10 | Defensa: 0', 10, 0),
                                                                                                                                         ('Disparo Rapido',       'ATAQUE', NULL,           150, 1, 100.00, 1, 'Poco Comun', 'Daño: 9 | Defensa: 0', 9, 0),
                                                                                                                                         ('Tajo Basico',          'ATAQUE', NULL,           50,  1, 100.00, 1, 'Comun',      'Daño: 5 | Defensa: 0', 5, 0),
                                                                                                                                         ('Golpe Pesado',         'ATAQUE', NULL,           150, 1, 100.00, 1, 'Poco Comun', 'Daño: 16 | Defensa: 0', 16, 0),
                                                                                                                                         ('Corte Doble',          'ATAQUE', NULL,           250, 1, 100.00, 1, 'Rara',       'Daño: 8 | Defensa: 0', 8, 0),
                                                                                                                                         ('Hachazo Brutal',       'ATAQUE', NULL,           150, 1, 100.00, 1, 'Poco Comun', 'Daño: 18 | Defensa: 0', 18, 0),
                                                                                                                                         ('Tiro Preciso',         'ATAQUE', NULL,           250, 1, 100.00, 1, 'Rara',       'Daño: 6 | Defensa: 0', 6, 0),
                                                                                                                                         ('Rompe Rodillas',       'ATAQUE', NULL,           150, 1, 100.00, 1, 'Poco Comun', 'Daño: 5 | Defensa: 0', 5, 0),
                                                                                                                                         ('Estocada Venenosa',    'ATAQUE', 'VENENO',       250, 2, 100.00, 3, 'Rara',       'Daño: 4 | Defensa: 0', 4, 0),
                                                                                                                                         ('Golpe Bajo',           'ATAQUE', 'ATURDIMIENTO', 150, 1, 50.00,  1, 'Poco Comun', 'Daño: 6 | Defensa: 0', 6, 0),
                                                                                                                                         ('Frenesi Sangriento',   'ATAQUE', 'HEMORRAGIA',   500, 3, 100.00, 1, 'Exotica',    'Daño: 3 | Defensa: 0', 3, 0),
                                                                                                                                         ('Tajo Cruzado',         'ATAQUE', 'HEMORRAGIA',   250, 2, 100.00, 1, 'Rara',       'Daño: 9 | Defensa: 0', 9, 0),
                                                                                                                                         ('Machacar',             'ATAQUE', 'ESCUDO',       150, 1, 100.00, 1, 'Poco Comun', 'Daño: 10 | Defensa: 0', 10, 0),
                                                                                                                                         ('Empujon',              'ATAQUE', NULL,           50,  1, 100.00, 1, 'Comun',      'Daño: 3 | Defensa: 0', 3, 0),
                                                                                                                                         ('Guillotina',           'ATAQUE', NULL,           1000,5, 100.00, 1, 'Legendaria', 'Daño: 25 | Defensa: 0', 25, 0),
                                                                                                                                         ('Cuchillo Arrojadizo',  'ATAQUE', 'ROBO',         150, 1, 100.00, 1, 'Poco Comun', 'Daño: 3 | Defensa: 0', 3, 0),
                                                                                                                                         ('Ataque Furtivo',       'ATAQUE', NULL,           250, 2, 100.00, 1, 'Rara',       'Daño: 12 | Defensa: 0', 12, 0),
                                                                                                                                         ('Martillazo Sismico',   'ATAQUE', NULL,           500, 3, 100.00, 2, 'Exotica',    'Daño: 8 | Defensa: 0', 8, 0),
                                                                                                                                         ('Embestida con Escudo', 'ATAQUE', 'ESCUDO',       250, 2, 100.00, 1, 'Rara',       'Daño: 5 | Defensa: 0', 5, 0),
                                                                                                                                         ('Desgarrar',            'ATAQUE', 'HEMORRAGIA',   150, 1, 100.00, 1, 'Poco Comun', 'Daño: 6 | Defensa: 0', 6, 0),
                                                                                                                                         ('Cuchillada Toxica',    'ATAQUE', 'VENENO',       500, 3, 100.00, 1, 'Exotica',    'Daño: 5 | Defensa: 0', 5, 0),
                                                                                                                                         ('Golpe de Gracia',      'ATAQUE', 'ROBO',         250, 2, 100.00, 1, 'Rara',       'Daño: 12 | Defensa: 0', 12, 0),
                                                                                                                                         ('Lanzamiento de Hacha', 'ATAQUE', NULL,           50,  1, 100.00, 1, 'Comun',      'Daño: 10 | Defensa: 0', 10, 0),
                                                                                                                                         ('Ataque Oportunista',   'ATAQUE', NULL,           250, 2, 100.00, 1, 'Rara',       'Daño: 8 | Defensa: 0', 8, 0),
                                                                                                                                         ('Danza de Espadas',     'ATAQUE', NULL,           500, 3, 100.00, 1, 'Exotica',    'Daño: 8 | Defensa: 0', 8, 0),
                                                                                                                                         ('Golpe Espectral',      'ATAQUE', NULL,           500, 3, 100.00, 1, 'Exotica',    'Daño: 10 | Defensa: 0', 10, 0),
                                                                                                                                         ('Granada Sagrada',      'ATAQUE', NULL,           1000,6, 100.00, 1, 'Legendaria', 'Daño: 40 | Defensa: 0', 40, 0),
                                                                                                                                         ('Vampirismo',           'ATAQUE', 'CURA',         250, 2, 100.00, 1, 'Rara',       'Daño: 8 | Defensa: 0', 8, 0);

-- ==========================================
-- DATA: CARTAS - DEFENSA
-- ==========================================
INSERT INTO Carta (nombre, tipo, subtipo, valorOroBase, nivelDesbloqueo, probabilidad, duracion, rareza, descripcion, dano, defensa) VALUES
                                                                                                                                         ('Escudo de Madera',     'DEFENSA', 'ESCUDO',  50,  1, 100.00, 1, 'Comun',      'Daño: 0 | Defensa: 4', 0, 4),
                                                                                                                                         ('Instinto',             'DEFENSA', NULL,      500, 3, 100.00, 1, 'Exotica',    'Daño: 0 | Defensa: 2', 0, 2),
                                                                                                                                         ('Piel de Hierro',       'DEFENSA', NULL,      250, 2, 80.00,  1, 'Rara',       'Daño: 0 | Defensa: 10', 0, 10),
                                                                                                                                         ('Bloqueo Perfecto',     'DEFENSA', 'ESCUDO',  150, 1, 100.00, 1, 'Poco Comun', 'Daño: 0 | Defensa: 10', 0, 10),
                                                                                                                                         ('Evasion Acrobatica',   'DEFENSA', 'ROBO',    250, 2, 100.00, 1, 'Rara',       'Daño: 0 | Defensa: 5', 0, 5),
                                                                                                                                         ('Postura Defensiva',    'DEFENSA', 'ESCUDO',  250, 2, 100.00, 3, 'Rara',       'Daño: 0 | Defensa: 15', 0, 15),
                                                                                                                                         ('Escudo de Pinchos',    'DEFENSA', 'ESCUDO',  150, 1, 100.00, 1, 'Poco Comun', 'Daño: 3 | Defensa: 4', 3, 4),
                                                                                                                                         ('Anticipacion',         'DEFENSA', 'ESCUDO',  150, 1, 100.00, 1, 'Poco Comun', 'Daño: 0 | Defensa: 5', 0, 5),
                                                                                                                                         ('Parada Defensiva',     'DEFENSA', 'ATURDIMIENTO', 250, 2, 100.00, 1, 'Rara',  'Daño: 0 | Defensa: 6', 0, 6),
                                                                                                                                         ('Muro de Piedra',       'DEFENSA', 'ESCUDO',  150, 1, 100.00, 1, 'Poco Comun', 'Daño: 0 | Defensa: 15', 0, 15),
                                                                                                                                         ('Reflejos Felinos',     'DEFENSA', NULL,      500, 3, 100.00, 3, 'Exotica',    'Daño: 0 | Defensa: 5', 0, 5),
                                                                                                                                         ('Ocultarse',            'DEFENSA', NULL,      500, 3, 100.00, 1, 'Exotica',    'Daño: 0 | Defensa: 5', 0, 5),
                                                                                                                                         ('Guardia Absoluta',     'DEFENSA', 'ESCUDO',  1000,5, 100.00, 1, 'Legendaria', 'Daño: 0 | Defensa: 30', 0, 30),
                                                                                                                                         ('Sacrificio de Sangre', 'DEFENSA', 'ESCUDO',  250, 1, 100.00, 1, 'Rara',       'Daño: 0 | Defensa: 8', 0, 8),
                                                                                                                                         ('Absorcion de Impacto', 'DEFENSA', 'CURA',    500, 3, 50.00,  1, 'Exotica',    'Daño: 0 | Defensa: 5', 0, 5),
                                                                                                                                         ('Espejismo',            'DEFENSA', NULL,      250, 2, 20.00,  1, 'Rara',       'Daño: 0 | Defensa: 3', 0, 3),
                                                                                                                                         ('Escudo Torre',         'DEFENSA', 'ESCUDO',  500, 3, 100.00, 4, 'Exotica',    'Daño: 0 | Defensa: 8', 0, 8),
                                                                                                                                         ('Venganza Ciega',       'DEFENSA', 'ESCUDO',  150, 1, 100.00, 1, 'Poco Comun', 'Daño: 0 | Defensa: 4', 0, 4),
                                                                                                                                         ('Armadura de Espinas',  'DEFENSA', NULL,      1000,4, 100.00, 99,'Legendaria', 'Daño: 0 | Defensa: 5', 0, 5),
                                                                                                                                         ('Muro de Hielo',        'DEFENSA', 'ESCUDO',  250, 2, 100.00, 3, 'Rara',       'Daño: 0 | Defensa: 4', 0, 4);

-- ==========================================
-- DATA: CARTAS - HECHIZOS
-- ==========================================
INSERT INTO Carta (nombre, tipo, subtipo, valorOroBase, nivelDesbloqueo, probabilidad, duracion, rareza, descripcion, dano, defensa) VALUES
                                                                                                                                         ('Pocion Curativa',           'HECHIZO', 'CURA',         50,  1, 100.00, 1,  'Comun',      'Efecto: +15 Vida', 0, 0),
                                                                                                                                         ('Bomba',                     'HECHIZO', NULL,           150, 1, 100.00, 1,  'Exotica',    'Daño: 15 | Defensa: 0', 15, 0),
                                                                                                                                         ('Vendaje',                   'HECHIZO', 'CURA',         50,  1, 100.00, 1,  'Comun',      'Efecto: +5 Vida', 0, 0),
                                                                                                                                         ('Afilador de Armas',         'HECHIZO', NULL,           150, 1, 100.00, 1,  'Poco Comun', 'Efecto: +3 Daño', 0, 0),
                                                                                                                                         ('Adrenalina',                'HECHIZO', NULL,           250, 2, 100.00, 1,  'Rara',       'Efecto: Roba 4 cartas', 0, 0),
                                                                                                                                         ('Preparacion',               'HECHIZO', 'ROBO',         150, 1, 100.00, 1,  'Poco Comun', 'Efecto: Roba 2 cartas', 0, 0),
                                                                                                                                         ('Frasco de Acido',           'HECHIZO', NULL,           250, 2, 100.00, 1,  'Rara',       'Efecto: Rompe Armadura', 0, 0),
                                                                                                                                         ('Cortina de Humo',           'HECHIZO', NULL,           500, 3, 100.00, 2,  'Exotica',    'Efecto: Oculto', 0, 0),
                                                                                                                                         ('Pocion de Fuerza',          'HECHIZO', NULL,           250, 2, 100.00, 2,  'Rara',       'Efecto: +3 Daño', 0, 0),
                                                                                                                                         ('Antidoto',                  'HECHIZO', 'CURA',         150, 1, 100.00, 3,  'Poco Comun', 'Efecto: Inmune a Veneno', 0, 0),
                                                                                                                                         ('Polvo Cegador',             'HECHIZO', NULL,           250, 2, 75.00,  1,  'Rara',       'Efecto: Ciega al Zombi', 0, 0),
                                                                                                                                         ('Concentracion',             'HECHIZO', 'ROBO',         500, 3, 100.00, 1,  'Exotica',    'Efecto: Elige carta', 0, 0),
                                                                                                                                         ('Brebaje Berserker',         'HECHIZO', NULL,           500, 3, 100.00, 3,  'Exotica',    'Efecto: +50% Daño', 0, 0),
                                                                                                                                         ('Trampa para Osos',          'HECHIZO', 'ATURDIMIENTO', 250, 2, 100.00, 1,  'Rara',       'Daño: 10 | Defensa: 0', 10, 0),
                                                                                                                                         ('Reciclar',                  'HECHIZO', NULL,           1000,4, 100.00, 1,  'Legendaria', 'Efecto: Descarta y Roba', 0, 0),
                                                                                                                                         ('Catalizador de Sangre',     'HECHIZO', 'VENENO',       500, 3, 100.00, 1,  'Exotica',    'Efecto: Convierte Hemorragia', 0, 0),
                                                                                                                                         ('Planificacion',             'HECHIZO', 'ROBO',         150, 1, 100.00, 1,  'Poco Comun', 'Efecto: Roba 1 carta', 0, 0),
                                                                                                                                         ('Ritual Oscuro',             'HECHIZO', 'ROBO',         250, 2, 100.00, 1,  'Rara',       'Efecto: Reinicia Mano', 0, 0),
                                                                                                                                         ('Piel de Obsidiana',         'HECHIZO', 'ESCUDO',       250, 2, 100.00, 2,  'Rara',       'Daño: 0 | Defensa: 10', 0, 10),
                                                                                                                                         ('Contrato Demoniaco',        'HECHIZO', 'ROBO',         1000,5, 100.00, 1,  'Legendaria', 'Efecto: -15 HP, Roba 5', 0, 0);

-- ==========================================
-- VINCULAR IMÁGENES
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
UPDATE Carta SET imagen = 'Preparacion.png' WHERE nombre = 'Preparacion';
UPDATE Carta SET imagen = 'Afilado_de_Armas.png' WHERE nombre = 'Afilador de Armas';

-- CONFIGURACION DE RECOMPENSAS
INSERT INTO ConfiguracionJuego (clave, valor)
VALUES ('ORO_BASE', 20),
       ('EXPERIENCIA_BASE', 50);