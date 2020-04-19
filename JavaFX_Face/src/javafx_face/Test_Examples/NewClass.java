  /** 
    
                                                                                //profileface detection starts 1
MatOfRect profileface = new MatOfRect();
     Mat grayFrame2 = new Mat();
    
     // convert the frame in gray scale
     Imgproc.cvtColor(frame, grayFrame2, Imgproc.COLOR_BGR2GRAY);
     // equalize the frame histogram to improve the result
     Imgproc.equalizeHist(grayFrame2, grayFrame2);
    
     // compute minimum face size (20% of the frame height, in our case)
     if (this.absoluteFaceSize == 0)
     {
      int height = grayFrame2.rows();
      if (Math.round(height * 0.2f) > 0)
       {
        this.absoluteFaceSize = Math.round(height * 0.2f);
        }
      }
      // detect profileface
      this.profilefaceCascade.detectMultiScale(grayFrame2, profileface, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
      new Size(this.absoluteFaceSize, this.absoluteFaceSize), new Size());
      
      // each rectangle in faces is a face: draw them!
      Rect[] facesArray1 = profileface.toArray();
      for (int i = 0; i < facesArray1.length; i++)
      Imgproc.rectangle(frame, facesArray1[i].tl(), facesArray1[i].br(), new Scalar(0, 255, 0), 3);
      // put text on profile face
      for (Rect rect : profileface.toArray())
      Imgproc.putText(frame, "Left Profile Face", new Point(rect.x,rect.y-5), 1, 0.7, new Scalar(0,255,0));
       
                                                                                //profileface detection ends 1


                                                                                //profileface detection starts 2
    MatOfRect profileface = new MatOfRect();
     profilefaceCascade.detectMultiScale(frame, profileface);                
     for (Rect rect : profileface.toArray())
      {
       Imgproc.putText(frame, "Left Profile Face", new Point(rect.x,rect.y-5), 1, 0.7, new Scalar(0,255,0));               
       Imgproc.rectangle(frame, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
       new Scalar(0, 255, 0),3);
      }
                                                                                //profileface detection ends 2











*/