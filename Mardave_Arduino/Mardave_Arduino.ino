// Drive Motor
#define powerIN3 4
#define powerIN4 5
#define powerENB 3

// Direction Motor
#define dirIN1 7
#define dirIN2 8
#define dirENA 6

char data = 0;                // Variable for storing received data
char driveMode = 0;           // Vairable for storing the drive mode
int powerENBValue = 0;           // Varialble for storing mapped power value
char direc = 0;               // Variable for storing the direction

void setup() 
{
  Serial.begin(9600);         // Sets the data rate in bits per second (baud) for serial data transmission
  pinMode(powerIN3, OUTPUT);
  pinMode(powerIN4, OUTPUT);
  pinMode(powerENB, OUTPUT);
  pinMode(dirIN1, OUTPUT);
  pinMode(dirIN2, OUTPUT);
  pinMode(dirENA, OUTPUT);
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
        digitalWrite(powerIN3, HIGH);
        digitalWrite(powerIN4, LOW);
        break;

     case 'N':                   // Set the motor to neutral mode
        digitalWrite(powerIN3, LOW);
        digitalWrite(powerIN4, LOW);
        break;

     case 'D':                   // Set the motor to drive mode
        digitalWrite(powerIN3, LOW);
        digitalWrite(powerIN4, HIGH);

     case 'S':                   // Set straight
        digitalWrite(dirIN1, LOW);
        digitalWrite(dirIN2, LOW);
        break;

     case 'Q':                   // Turn Left
        digitalWrite(dirIN1, LOW);
        digitalWrite(dirIN2, HIGH);
        digitalWrite(dirENA, HIGH);
        break;

     case 'E':                   // Turn Right
        digitalWrite(dirIN1, HIGH);
        digitalWrite(dirIN2, LOW);
        digitalWrite(dirENA, HIGH);
        break;

     case 48:
        analogWrite(powerENB, 0);
    }
    
    if(int(data) > 48 && int(data) < 58)   // Adjusting power
    {
      powerENBValue = map(int(data), 49, 57 , 50, 180);
      analogWrite(powerENB, powerENBValue);
      data == 0;
    }
  }
}
