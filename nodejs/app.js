const express = require('express');
const mysql = require('mysql2');
const bodyParser = require('body-parser');
const app = express();

const moment = require('moment-timezone');

// MySQL 연결 설정
const db = mysql.createConnection({
  host: '192.168.200.104',  // 서버 호스트 이름
  user: 'jio',       // MySQL 사용자 이름
  password: '6207', // MySQL 비밀번호
  database: 'knockdb'  // 사용할 데이터베이스 이름
});

// MySQL 연결
db.connect((err) => {
  if (err) {
    console.error('MySQL 연결 실패:', err);
  } else {
    console.log('MySQL 연결 성공');
  }
});

// BodyParser를 이용해 JSON 데이터를 파싱하도록 설정
app.use(bodyParser.json());

// 회원가입
app.post('/register', (req, res) => {
    console.log(req.body);
    const { user_id, user_pw, name, emergency_contact } = req.body;

    const sql = 'INSERT INTO members (user_id, user_pw, name, emergency_contact) VALUES (?, ?, ?, ?)';
    db.query(sql, [user_id, user_pw, name, emergency_contact ], (err, result) => {
        if (err) {
            console.error(err);
            return res.status(500).send("회원가입 실패");
        }
        // req.body가 올바르게 정의되었는지 확인
    
        res.status(200).send("회원가입 성공");
    });
});

// 로그인
app.post('/login', (req, res) => {
    console.log(req.body);
    const { user_id, user_pw } = req.body;

    const sql = 'SELECT pkid, user_id, user_pw, name, emergency_contact FROM members WHERE user_id = ? AND user_pw = ?';
    db.query(sql, [user_id, user_pw ], (err, result) => {
      if (err) {
          res.status(500).send('Error');
      } else if (result.length > 0) {
          // 로그인 성공 시 pkid 포함하여 응답
          res.json(result[0]);
          console.log(result[0]);
      } else {
          res.status(401).send('Invalid credentials');
      }
    });

});

// 긴급전화
app.get('/getEmergencyContact/:pkid', (req, res) => {
  const pkid = req.params.pkid;

  const sql = 'SELECT emergency_contact FROM members WHERE pkid = ?';
  db.query(sql, [pkid], (err, result) => {
    if (err) {
        console.error('쿼리 실행 중 오류:', err);
        res.status(500).send('Error');
    }

    if (result.length > 0) {
        res.json({ emergency_contact: result[0].emergency_contact });
    } else {
        console.log('연락처를 찾을 수 없습니다.');
        res.status(404).json({ message: '연락처를 찾을 수 없습니다.' });
    }

  });

});

// 할일 등록
app.post('/schedules', (req, res) => {
  const { fkmember, tasks, schedule_date } = req.body;

  const sql = 'INSERT INTO schedules (fkmember, tasks, schedule_date) VALUES (?, ?, ?)';
  db.query(sql, [fkmember, tasks, schedule_date], (err, result) => {
    if (err) {
      return res.status(500).json({ error: '할일 DB 삽입 오류' });
    }
    res.status(200).json({ message: '할일 DB 삽입 성공' });
  });

});


// 할일 가져와서 전송
app.get('/getTasks/:fkmember/:schedule_date', (req, res) => {
  const { fkmember, schedule_date } = req.params;
  const sql = 'SELECT tasks FROM schedules WHERE (fkmember = ?) AND (schedule_date = ?)';

  db.query(sql, [fkmember, schedule_date], (error, results) => {
      if (error) {
          console.error('데이터 가져오기 오류:', error);
          return res.status(500).send('서버 오류');
      }
      
      // 결과를 클라이언트에 JSON 형식으로 전송
      res.json(results);
  });
});


// 정보 수정
app.post('/update', (req, res) => {
  console.log(req.body);
  const { pkid, name, emergency_contact } = req.body;

  const sql = 'update members SET name = ?, emergency_contact = ? where pkid = ?;';
  db.query(sql, [ name, emergency_contact, pkid ], (err, result) => {
      if (err) {
          console.error(err);
          return res.status(500).send("정보 수정 실패");
      }
      // req.body가 올바르게 정의되었는지 확인
  
      res.status(200).send("정보 수정 성공");
  });
});

// 약 정보 저장
app.post('/medications', (req, res) => {
  const { fkmember, med_name, time_of_day } = req.body;

  if (!fkmember || !med_name || !time_of_day) {
      return res.status(400).send('All fields are required');
  }

  const sql = 'INSERT INTO medications (fkmember, med_name, time_of_day) VALUES (?, ?, ?)';
  db.query(sql, [fkmember, med_name, time_of_day], (err, result) => {
      if (err) {
          return res.status(500).send('Database error');
      }
      res.status(201).send('Medications saved');
  });
});


// 약 정보 가져오기
app.get('/medications/:fkmember/:time_of_day', (req, res) => {
  const { fkmember, time_of_day } = req.params;
  const sql = 'select med_name from medications where (fkmember = ?) and (time_of_day = ?)';

  db.query(sql, [fkmember, time_of_day], (error, results) => {
      if (error) {
          console.error('데이터 가져오기 오류:', error);
          return res.status(500).send('서버 오류');
      }
      
      res.json(results);
  });
});


// 출입 기록 저장
app.get('/recordAccess', (req, res) => {
  const fkmember = req.query.fkmember;

  if (!fkmember) {
      return res.status(400).send('fkmember 파라미터가 필요합니다.');
  }

  const sql = 'INSERT INTO door_access (fkmember) VALUES (?)';
  db.query(sql, [fkmember], (err, result) => {
      if (err) {
          console.error(err);
          return res.status(500).send('DB 저장에 실패했습니다.');
      }
      res.send('출입 기록이 성공적으로 저장되었습니다.');
  });
});


// 출입 기록 가져오기
app.get('/getAccessRecords', (req, res) => {
  const fkmember = req.query.fkmember;

  const sql = 'SELECT access_timestamp FROM door_access WHERE fkmember = ?';
  db.query(sql, [fkmember], (err, results) => {
      if (err) {
          console.error(err);
          return res.status(500).send('출입 기록을 가져오는 데 실패했습니다.');
      }
      const records = results.map(record => {
        // timestamp를 한국 표준시로 변환
        const localTime = moment.tz(record.access_timestamp, 'Asia/Seoul').format('YYYY-MM-DD HH:mm:ss');
        return { access_timestamp: localTime }; // 변환된 시간을 새로운 필드에 추가
    });
      res.json(records); // JSON 형식으로 출입 기록 반환
      console.log(records);
  });
});



/*


// 간단한 API 엔드포인트 생성 (데이터 가져오기)
app.get('/members', (req, res) => {
  const sqlQuery = 'SELECT * FROM members';
  db.query(sqlQuery, (err, results) => {
    if (err) {
      res.status(500).send(err);
    } else {
      res.json(results);
    }
  });
});
*/

// 서버 실행
app.listen(3000, () => {
  console.log('서버 실행 중: http://localhost:3000');
});