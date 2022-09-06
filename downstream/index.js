import express from 'express';
import crypto from 'crypto';
import { LoremIpsum } from "lorem-ipsum";
import { uniqueNamesGenerator, names } from 'unique-names-generator';

const app = express();
const port = 3000;

app.get('/fleet-manager/vehicles', (req, res) => {

  const lorem = new LoremIpsum({
    sentencesPerParagraph: {
      max: 8,
      min: 4
    },
    wordsPerSentence: {
      max: 16,
      min: 4
    }
  });

  const response = { vehicles : [] };

  const createVehicle = (licencePlate) => {
    return {
      vin: 'SUP00000000000000',
      licencePlate: licencePlate,
      driverId: crypto.randomUUID(),
      description: lorem.generateParagraphs(10)
    };
  };

  if(Array.isArray(req.query.licencePlate)) {
      req.query.licencePlate.forEach( plate => {
        response.vehicles.push(createVehicle(plate));
      });
  } else {
    response.vehicles.push(createVehicle(req.query.licencePlate));
  }

  //simulated delay
  setTimeout(() => {
    res.send(response);
  }, 50 + 10 * response.vehicles.length);
  
});

app.get('/hr-db/employees/:id', (req, res) => {

  const config = {
    dictionaries: [names]
  }

  const response = {
    firstName: uniqueNamesGenerator(config),
    lastName: uniqueNamesGenerator(config),
    id: crypto.randomUUID()
  };

  //simulated delay
  setTimeout(() => {
    res.send(response);
  }, 50);

});

app.get('/police-register/driving-fines', (req, res) => {

  const response = {
    driverId: crypto.randomUUID(),
    violations: [
      {
        violationType: "SPEEDING",
        points: Math.ceil(Math.random() * 10),
        dateTime: new Date().toISOString(),
        carLicencePlate: crypto.randomUUID()
      },
      {
        violationType: "SPEEDING",
        points: Math.ceil(Math.random() * 10),
        dateTime: new Date().toISOString(),
        carLicencePlate: crypto.randomUUID()
      },
      {
        violationType: "SPEEDING",
        points: Math.ceil(Math.random() * 10),
        dateTime: new Date().toISOString(),
        carLicencePlate: crypto.randomUUID()
      }
    ]
  };

  //simulated delay
  setTimeout(() => {
    res.send(response);
  }, 100);

});


app.listen(port, () => {
  console.log(`Mocked downstream APIs started on port ${port}`);
});