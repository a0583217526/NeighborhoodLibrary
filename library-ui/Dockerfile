FROM node:18-alpine
WORKDIR /app

# העתקת קבצי ההגדרות והתקנת התלויות
COPY package*.json ./
RUN npm install

# העתקת שאר קבצי ה-React
COPY . .

# חשיפת הפורט של React
EXPOSE 3000

# הרצת שרת הפיתוח של React
CMD ["npm", "start"]