// Right-side Motors
#define rightENA 3
#define rightIN1 4
#define rightIN2 5

// Left-side Motors
#define leftENB 6
#define leftIN3 7
#define leftIN4 8

char data = 0;                // Variable for storing received data
char driveMode = 0;           // Vairable for storing the drive mode
int rightENAValue = 0;        // Varialble for storing mapped power value
char direc = 0;               // Variable for storing the direction

void setup() 
{
  Serial.begin(9600);         // Sets the data rate in bits per second (baud) for serial data transmission
  pinMode(rightIN1, OUTPUT);
  pinMode(rightIN2, OUTPUT);
  pinMode(rightENA, OUTPUT);
  pinMode(leftIN3, OUTPUT);
  pinMode(leftIN4, OUTPUT);
  pinMode(leftENB, OUTPUT);
}
void loop()
{
  if(Serial.available() > 0)  // Send data only when you receive data:
  {
    data = Serial.read();      // Read the incoming data and store it into variable data
    Serial.print(data);        // Print Value inside data in Serial monitor
    Serial.print("\n");        // New line 

    switch(data) {

      case 'R':                  // Set the motor to reverse mode
        digitalWrite(rightIN1, LOW);
        digitalWrite(rightIN2, HIGH);
        digitalWrite(leftIN3, LOW);
        digitalWrite(leftIN4, HIGH);
        break;

     case 'N':                   // Set the motor to neutral mode
        digitalWrite(rightIN1, LOW);
        digitalWrite(rightIN2, LOW);
        digitalWrite(leftIN3, LOW);
        digitalWrite(leftIN4, LOW);
        break;

     case 'D':                   // Set the motor to drive mode
        digitalWrite(rightIN1, HIGH);
        digitalWrite(rightIN2, LOW);
        digitalWrite(leftIN3, HIGH);
        digitalWrite(leftIN4, LOW);
        break;

     case 'S':                   // Set straight
        if(rightIN1 == LOW && rightIN2 == HIGH && leftIN3 == LOW && leftIN4 == HIGH) {
          digitalWrite(rightIN1, LOW);
          digitalWrite(rightIN2, HIGH);
          digitalWrite(leftIN3, LOW);
          digitalWrite(leftIN4, HIGH);
        } else if (rightIN1 == HIGH && rightIN2 == LOW && leftIN3 == HIGH && leftIN4 == LOW) {
          digitalWrite(rightIN1, HIGH);
          digitalWrite(rightIN2, LOW);
          digitalWrite(leftIN3, HIGH);
          digitalWrite(leftIN4, LOW);
        } else if (rightIN1 == LOW && rightIN2 == LOW && leftIN3 == LOW && leftIN4 == LOW) {
          digitalWrite(rightIN1, LOW);
          digitalWrite(rightIN2, LOW);
          digitalWrite(leftIN3, LOW);
          digitalWrite(leftIN4, LOW);
        }
        break;

     case 'Q':                   // Turn Left
        if(rightIN1 == LOW && rightIN2 == HIGH && leftIN3 == LOW && leftIN4 == HIGH) {
          digitalWrite(rightIN1, LOW);
          digitalWrite(rightIN2, HIGH);
          analogWrite(rightENA, 100);
        } else if (rightIN1 == HIGH && rightIN2 == LOW && leftIN3 == HIGH && leftIN4 == LOW) {
          digitalWrite(rightIN1, HIGH);
          digitalWrite(rightIN2, LOW);
          analogWrite(rightENA, 140);
        }
        break;

     case 'E':                   // Turn Right
        if(rightIN1 == LOW && rightIN2 == HIGH && leftIN3 == LOW && leftIN4 == HIGH) {
          digitalWrite(leftIN3, LOW);
          digitalWrite(leftIN4, HIGH);
          digitalWrite(leftENB, 100);
        } else if (rightIN1 == HIGH && rightIN2 == LOW && leftIN3 == HIGH && leftIN4 == LOW) {
          digitalWrite(leftIN3, HIGH);
          digitalWrite(leftIN4, LOW);
          digitalWrite(leftENB, 140);
        }
        break;

     case 48:
        analogWrite(rightENA, 0);
        analogWrite(leftENB, 0);
        break;
    }
    
    if(int(data) > 48 && int(data) < 58)   // Adjusting power
    {
      rightENAValue = map(int(data), 49, 57 , 50, 180);
      analogWrite(rightENA, rightENAValue);
      analogWrite(leftENB, rightENAValue);
      data == 0;
    }
  }
}
