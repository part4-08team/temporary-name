-- 1) 온도 구간 삽입
INSERT INTO temperature_category (name, min_temp, max_temp)
VALUES
    ('VERY_HOT',   28.0,   50.0),
    ('HOT',        23.0,   28.0),
    ('WARM',       20.0,   23.0),
    ('COOL',       17.0,   20.0),
    ('CHILLY',     12.0,   17.0),
    ('COLD',        9.0,   12.0),
    ('VERY_COLD',   5.0,    9.0),
    ('FREEZING',  -50.0,    5.0);

SELECT id, name, min_temp, max_temp
FROM temperature_category
ORDER BY name;

-- 2) 온도 구간 ↔ 상위 타입 매핑 일괄 삽입
WITH cat AS (
    SELECT id, name
    FROM temperature_category
    WHERE name IN (
                   'VERY_HOT','HOT','WARM','COOL',
                   'CHILLY','COLD','VERY_COLD','FREEZING'
        )
)
INSERT INTO category_allowed_type (category_id, clothes_type)
SELECT
    c.id,
    v.clothes_type
FROM cat c
         JOIN (
    VALUES
        ('VERY_HOT',   'TOP'),    ('VERY_HOT',   'BOTTOM'), ('VERY_HOT',   'SHOES'),
        ('HOT',        'TOP'),    ('HOT',        'BOTTOM'), ('HOT',        'SHOES'),
        ('WARM',       'TOP'),    ('WARM',       'BOTTOM'), ('WARM',       'OUTER'),  ('WARM',       'SHOES'),
        ('COOL',       'TOP'),    ('COOL',       'BOTTOM'), ('COOL',       'OUTER'),  ('COOL',       'SHOES'),
        ('CHILLY',     'TOP'),    ('CHILLY',     'BOTTOM'), ('CHILLY',     'OUTER'),  ('CHILLY',     'SHOES'),
        ('COLD',       'TOP'),    ('COLD',       'BOTTOM'), ('COLD',       'OUTER'),  ('COLD',       'SHOES'),
        ('VERY_COLD',  'TOP'),    ('VERY_COLD',  'BOTTOM'), ('VERY_COLD',  'OUTER'),  ('VERY_COLD',  'SHOES'),
        ('FREEZING',   'TOP'),    ('FREEZING',   'BOTTOM'), ('FREEZING',   'OUTER'),
        ('FREEZING',   'SCARF'),  ('FREEZING',   'ACCESSORY'),('FREEZING',   'HAT'),    ('FREEZING',   'SHOES')
) AS v(clothes_cat, clothes_type)
              ON v.clothes_cat = c.name;

SELECT
    tc.name   AS category_name,
    t.clothes_type
FROM category_allowed_type t
         JOIN temperature_category tc ON tc.id = t.category_id
ORDER BY tc.name, t.clothes_type;

-- 3) 온도 구간 + 타입 ↔ 세부 옵션 일괄 삽입
WITH cat AS (
    SELECT id, name
    FROM temperature_category
    WHERE name IN (
                   'VERY_HOT','HOT','WARM','COOL',
                   'CHILLY','COLD','VERY_COLD','FREEZING'
        )
)
INSERT INTO category_allowed_detail (category_id, clothes_type, detail_value)
SELECT
    c.id,
    v.clothes_type,
    v.detail_value
FROM cat c
         JOIN (
    VALUES
        -- VERY_HOT
        ('VERY_HOT','TOP',    '반팔'),
        ('VERY_HOT','BOTTOM', '반바지'),
        ('VERY_HOT','SHOES',  '슬리퍼/샌들'),
        ('VERY_HOT','SHOES',  '스포츠화'),

        -- HOT
        ('HOT',     'TOP',    '반팔'),
        ('HOT',     'BOTTOM', '반바지'),
        ('HOT',     'SHOES',  '스니커즈'),
        ('HOT',     'SHOES',  '슬리퍼/샌들'),
        ('HOT',     'SHOES',  '스포츠화'),

        -- WARM
        ('WARM',    'TOP',    '반팔'),
        ('WARM',    'TOP',    '긴팔'),
        ('WARM',    'BOTTOM', '반바지'),
        ('WARM',    'BOTTOM', '긴바지'),
        ('WARM',    'OUTER',  '카디건'),
        ('WARM',    'OUTER',  '후드 집업'),
        ('WARM',    'SHOES',  '스니커즈'),
        ('WARM',    'SHOES',  '구두'),
        ('WARM',    'SHOES',  '스포츠화'),

        -- COOL
        ('COOL',    'TOP',    '긴팔'),
        ('COOL',    'BOTTOM', '긴바지'),
        ('COOL',    'OUTER',  '카디건'),
        ('COOL',    'OUTER',  '트러커 재킷'),
        ('COOL',    'OUTER',  '후드 집업'),
        ('COOL',    'SHOES',  '스니커즈'),
        ('COOL',    'SHOES',  '부츠/워커'),
        ('COOL',    'SHOES',  '구두'),

        -- CHILLY
        ('CHILLY',  'TOP',    '긴팔'),
        ('CHILLY',  'BOTTOM', '긴바지'),
        ('CHILLY',  'OUTER',  '트러커 재킷'),
        ('CHILLY',  'OUTER',  '코트'),
        ('CHILLY',  'SHOES',  '부츠/워커'),

        -- COLD
        ('COLD',    'TOP',    '긴팔'),
        ('COLD',    'BOTTOM', '긴바지'),
        ('COLD',    'OUTER',  '코트'),
        ('COLD',    'OUTER',  '패딩'),
        ('COLD',    'SHOES',  '부츠/워커'),

        -- VERY_COLD
        ('VERY_COLD','TOP',   '긴팔'),
        ('VERY_COLD','BOTTOM','긴바지'),
        ('VERY_COLD','OUTER', '패딩'),
        ('VERY_COLD','SHOES', '부츠/워커'),

        -- FREEZING
        ('FREEZING','TOP',       '긴팔'),
        ('FREEZING','BOTTOM',    '긴바지'),
        ('FREEZING','OUTER',     '패딩'),
        ('FREEZING','SCARF',     '목도리'),
        ('FREEZING','ACCESSORY', '장갑'),
        ('FREEZING','HAT',       '비니'),
        ('FREEZING','HAT',       '트루퍼'),
        ('FREEZING','SHOES',     '부츠/워커'),
        ('FREEZING','SHOES',     '패딩/퍼 신발')
) AS v(clothes_cat, clothes_type, detail_value)
              ON v.clothes_cat = c.name;

SELECT
    tc.name         AS category_name,
    d.clothes_type,
    d.detail_value
FROM category_allowed_detail d
         JOIN temperature_category tc ON tc.id = d.category_id
ORDER BY tc.name, d.clothes_type, d.detail_value;


