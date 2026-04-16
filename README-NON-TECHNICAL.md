# Invoice Generator - Simple Local Setup Guide

This guide is for non-technical users who want to run the project on their own computer.

## What this project does

- Opens a simple web screen to fill invoice details
- Generates a professional PDF invoice
- Downloads the PDF to your computer

## Before you start

You need these installed once:

1. **Java 17 or higher**
2. **Maven**
3. **Node.js (LTS) + npm**

If any command below is not recognized, install that software first.

## Step-by-step run instructions

Open **Terminal** and run the commands exactly as shown.

### 1) Go to project folder

```bash
cd /Users/sanyrawat/git/billing-invoice
```

### 2) Start backend (Spring Boot)

```bash
mvn clean install
mvn spring-boot:run
```

Keep this terminal open.  
When backend starts, it runs on:

- `http://localhost:8080`

Swagger testing page:

- `http://localhost:8080/swagger-ui.html`

### 3) Open a new terminal and start frontend (UI)

```bash
cd /Users/sanyrawat/git/billing-invoice/frontend
npm install
npm run dev
```

Frontend opens on:

- `http://localhost:5173`

## How to use

1. Open `http://localhost:5173`
2. Fill invoice form fields
3. Click **Generate Invoice PDF**
4. PDF file downloads automatically

## If something does not start

### Check backend

- Make sure backend terminal shows `Started InvoicePdfGeneratorApplication`
- If port already in use, stop old app and rerun:

```bash
lsof -i :8080 -t
kill <PID>
mvn spring-boot:run
```

### Check frontend

- Make sure frontend terminal shows `Local: http://localhost:5173`
- If port already in use:

```bash
lsof -i :5173 -t
kill <PID>
npm run dev
```

## How to stop the application

In both terminals, press:

- `Ctrl + C`

