-- 1) attributes 테이블에 definition_name 삽입 (중복 방지)
INSERT INTO attributes (id, definition_name)
VALUES
    (gen_random_uuid(), '모자 종류'),
    (gen_random_uuid(), '의상 상세 종류(아우터)'),
    (gen_random_uuid(), '의상 상세 종류(상의)'),
    (gen_random_uuid(), '의상 상세 종류(신발)'),
    (gen_random_uuid(), '의상 상세 종류(하의)')
    ON CONFLICT (definition_name) DO NOTHING;

-- 2) 삽입할 데이터를 CTE로 준비
WITH data(definition_name, value) AS (
    VALUES
        ('모자 종류',              '비니'),
        ('모자 종류',              '트루퍼'),
        ('의상 상세 종류(아우터)', '카디건'),
        ('의상 상세 종류(아우터)', '코트'),
        ('의상 상세 종류(아우터)', '패딩'),
        ('의상 상세 종류(아우터)', '트러커 재킷'),
        ('의상 상세 종류(아우터)', '후드 집업'),
        ('의상 상세 종류(아우터)', '레더/라이더스 재킷'),
        ('의상 상세 종류(아우터)', '슈트/ 블레이저'),
        ('의상 상세 종류(상의)',   '반팔'),
        ('의상 상세 종류(상의)',   '긴팔'),
        ('의상 상세 종류(상의)',   '니트/스웨터'),
        ('의상 상세 종류(신발)',   '구두'),
        ('의상 상세 종류(신발)',   '스니커즈'),
        ('의상 상세 종류(신발)',   '스포츠화'),
        ('의상 상세 종류(신발)',   '슬리퍼/샌들'),
        ('의상 상세 종류(신발)',   '부츠/워커'),
        ('의상 상세 종류(신발)',   '패딩/퍼 신발'),
        ('의상 상세 종류(하의)',   '긴바지'),
        ('의상 상세 종류(하의)',   '반바지'),
        ('의상 상세 종류(하의)',   '청바지')
)

-- 3) 삽입: UUID 함수로 id 생성, definition_id 는 위에서 채운 attributes 에서 조회
INSERT INTO attribute_selectable_value (id, definition_id, value)
SELECT
    gen_random_uuid(),  -- id
    a.id,               -- definition_id
    d.value             -- value
FROM data d
         JOIN attributes a
              ON a.definition_name = d.definition_name;
