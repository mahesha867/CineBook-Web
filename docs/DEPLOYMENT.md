# 🚀 CineBook Web — Production Deployment Guide

This guide covers all recommended methods to deploy **CineBook Web** (Spring Boot 3 + Java 21 + MySQL 8.0).

---

## 📋 Architecture Requirements Before Deploying

CineBook Web relies on:
1. **Java 21 JDK / JRE**
2. **MySQL 8.0+** with support for:
   - Foreign Keys (`ON DELETE CASCADE` / `RESTRICT`)
   - Stored Procedures (`sp_release_expired_holds`, `sp_cancel_booking`)
   - Triggers (`trg_after_booking_insert`, `trg_after_booking_cancel`)
   - Views with Window Functions (`DENSE_RANK()`)
   - Row-level locking (`SELECT ... FOR UPDATE`)

---

## 🌟 Method 1: Docker & Docker Compose (Any VPS / Cloud Server)
> **Recommended for:** AWS EC2, DigitalOcean Droplet, Linode, Hetzner, or local testing.

With Docker, both MySQL 8 and the Spring Boot application start with automated database initialization in one command.

### 1. Prerequisites
- Docker & Docker Compose installed on your host.

### 2. Deployment Steps
```bash
# 1. Clone repository
git clone https://github.com/mahesha867/CineBook-Web.git
cd CineBook-Web

# 2. Build and run in detached mode
docker compose up -d --build

# 3. View logs
docker compose logs -f app
```

### 3. Verify
- Customer Portal: `http://<your-server-ip>:8080`
- Admin Dashboard: `http://<your-server-ip>:8080/admin.html` (Credentials: `admin` / `admin123`)

---

## ☁️ Method 2: Railway.app (Fastest Cloud PaaS Deployment)
> **Recommended for:** Zero-infrastructure cloud deployment with full MySQL 8 support.

### Step 1: Push Code to GitHub
Ensure your latest code (including the Dockerfile) is committed and pushed to GitHub:
```bash
git add .
git commit -m "Add Docker and deployment configuration"
git push origin main
```

### Step 2: Deploy MySQL 8 on Railway
1. Go to [railway.app](https://railway.app) and create a new project.
2. Click **+ New** -> **Database** -> **Add MySQL**.
3. Railway will provision a MySQL 8 database. Note the connection details under the **Connect** tab:
   - Host, Port, Username, Password, Database Name.

### Step 3: Run Database Scripts on Railway MySQL
Using your preferred database client (e.g., MySQL Workbench, DBeaver, or terminal):
```bash
# Connect using Railway's public MySQL endpoint:
mysql -h <RAILWAY_HOST> -P <RAILWAY_PORT> -u root -p < database/01_schema.sql
mysql -h <RAILWAY_HOST> -P <RAILWAY_PORT> -u root -p < database/02_seed.sql
mysql -h <RAILWAY_HOST> -P <RAILWAY_PORT> -u root -p < database/03_views_procs_triggers.sql
```

### Step 4: Deploy CineBook Web Service
1. In the same Railway project, click **+ New** -> **GitHub Repo** -> select `CineBook-Web`.
2. Under **Variables**, add:
   - `DB_URL`: `jdbc:mysql://${{MySQL.MYSQLHOST}}:${{MySQL.MYSQLPORT}}/${{MySQL.MYSQLDATABASE}}?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`
   - `DB_USER`: `${{MySQL.MYSQLUSER}}`
   - `DB_PASSWORD`: `${{MySQL.MYSQLPASSWORD}}`
   - `PORT`: `${{PORT}}`
3. Under **Settings** -> **Networking**, click **Generate Domain**.
4. Railway will build the Docker container and provide a live `https://...up.railway.app` URL!

---

## 🌐 Method 3: Render.com
> **Recommended for:** Free/low-cost cloud web service.

### Step 1: Provision MySQL 8
Render does not offer a free managed MySQL service. You can use a free MySQL provider like [Aiven.io](https://aiven.io) (Free 5GB MySQL 8):
1. Sign up on [Aiven.io](https://aiven.io) and create a **MySQL 8** service.
2. In Aiven, download the CA certificate or enable public access.
3. Run the 3 SQL scripts (`database/01_schema.sql`, `02_seed.sql`, `03_views_procs_triggers.sql`) against your Aiven MySQL database.

### Step 2: Deploy Spring Boot on Render
1. Go to [render.com](https://render.com) -> **New +** -> **Web Service**.
2. Connect your `CineBook-Web` GitHub repository.
3. Configure settings:
   - **Runtime**: `Docker` (Render will detect the root `Dockerfile`).
   - **Instance Type**: Free or Starter.
4. Add **Environment Variables**:
   - `DB_URL`: `jdbc:mysql://<AIVEN_HOST>:<AIVEN_PORT>/cinebook_web?useSSL=true&requireSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`
   - `DB_USER`: `<AIVEN_USER>`
   - `DB_PASSWORD`: `<AIVEN_PASSWORD>`
5. Click **Deploy Web Service**.

---

## 🖥️ Method 4: Bare-Metal / Linux VM (Ubuntu / Debian / CentOS)
> **Recommended for:** Direct server deployment with Systemd and Nginx.

### 1. Install Java 21 & MySQL 8
```bash
sudo apt update
sudo apt install -y openjdk-21-jdk mysql-server nginx
```

### 2. Configure MySQL Database
```bash
sudo mysql
```
```sql
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'YourSecurePassword!';
FLUSH PRIVILEGES;
EXIT;
```
Import database schema and seed data:
```bash
mysql -u root -p < database/01_schema.sql
mysql -u root -p < database/02_seed.sql
mysql -u root -p < database/03_views_procs_triggers.sql
```

### 3. Build & Package Spring Boot Jar
```bash
cd backend
./mvnw clean package -DskipTests
```
The output JAR is at `backend/target/cinebook-web-1.0.0.jar`.

### 4. Create Systemd Service
Create `/etc/systemd/system/cinebook.service`:
```ini
[Unit]
Description=CineBook Web Spring Boot Service
After=syslog.target network.target mysql.service

[Service]
User=ubuntu
ExecStart=/usr/bin/java -jar /home/ubuntu/CineBook-Web/backend/target/cinebook-web-1.0.0.jar
Environment="DB_URL=jdbc:mysql://localhost:3306/cinebook_web?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
Environment="DB_USER=root"
Environment="DB_PASSWORD=YourSecurePassword!"
Environment="PORT=8080"
SuccessExitStatus=143
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

Enable and start the service:
```bash
sudo systemctl daemon-reload
sudo systemctl enable cinebook
sudo systemctl start cinebook
sudo systemctl status cinebook
```

### 5. (Optional) Nginx Reverse Proxy with SSL
Configure `/etc/nginx/sites-available/cinebook`:
```nginx
server {
    listen 80;
    server_name yourdomain.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```
Enable site and acquire free SSL with Certbot:
```bash
sudo ln -s /etc/nginx/sites-available/cinebook /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
sudo certbot --nginx -d yourdomain.com
```

---

## 🔍 Verification Checklist

After deployment, check these endpoints to ensure full system functionality:
- [ ] **Home Page**: `GET /` — movies and screen list render properly.
- [ ] **Seat Booking Flow**: Click a showtime, select seats, verify 5-minute seat hold lock.
- [ ] **Concessions & Checkout**: Add snacks, test coupon `CINE10`, verify QR code ticket generation.
- [ ] **Admin Dashboard**: `GET /admin.html` (Login: `admin` / `admin123`) — verify Chart.js analytics and views (`v_movie_revenue_analytics`, `v_top_selling_snacks`).
- [ ] **Ticket Lookup & Cancellation**: `GET /my-tickets.html` — test booking cancellation and 80% refund processing.

