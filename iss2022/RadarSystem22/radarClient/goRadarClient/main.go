package main

import (
	"fmt"
	"net"
	"os"
	"strconv"
	"time"
)

type Input struct {
	message string
	ack     chan bool
}

var input_channel = make(chan Input, 1)
var done = make(chan bool)

func connection_handler() {
	exit := false
	servAddr := "localhost:4001"
	tcpAddr, err := net.ResolveTCPAddr("tcp", servAddr)
	if err != nil {
		println("ResolveTCPAddr failed:", err.Error())
		os.Exit(1)
	}

	conn, err := net.DialTCP("tcp", nil, tcpAddr)
	if err != nil {
		println("Dial failed:", err.Error())
		os.Exit(1)
	}

	// test
	i, j := 0, 0
	for {
		i++
		j++
		if i > 100 {
			i = 0
		}
		if j >= 360 {
			j = 0
		}
		var msg = make([]byte, 64)
		msg = []byte("{\"distance\": " + strconv.Itoa(i) + ", \"angle\": " + strconv.Itoa(j) + "}")

		_, err = conn.Write(msg)
		if err != nil {
			println("Write to server failed:", err.Error())
			os.Exit(1)
		}

		time.Sleep(time.Duration(time.Millisecond * 30))
	}

	defer conn.Close()
	for !exit {
		select {
		case input := <-input_channel:
			if input.message == "quit" {
				exit = true
				break
			}

			fmt.Println("TEST" + input.message)
			l := len([]rune(input.message))
			fmt.Println(l)
			for i := l; i < 64; i++ {
				input.message += " "
			}
			fmt.Println(len([]rune(input.message)))

			var msg = make([]byte, 64)
			msg = []byte(input.message)

			_, err = conn.Write(msg)
			if err != nil {
				println("Write to server failed:", err.Error())
				os.Exit(1)
			}

			println("write to server = ", input.message)
			input.ack <- true
		default:
			continue
		}

		/*reply := make([]byte, 1024)

		_, err = conn.Read(reply)
		if err != nil {
			println("Write to server failed:", err.Error())
			os.Exit(1)
		}

		println("reply from server=", string(reply))*/
	}

	done <- true
}

func input() {
	// read from keyboard:
	input := Input{message: "", ack: make(chan bool)}
	var distance, angle int

	for {
		input.message = "{\"distance\": "

		fmt.Printf("Distance: ")
		fmt.Scanf("%d\n", &distance)
		if distance == -1 {
			input.message = "quit"
			input_channel <- input
			break
		}
		input.message += strconv.Itoa(distance)

		fmt.Printf("Angle: ")
		fmt.Scanf("%d\n", &angle)
		input.message += ", \"angle\": " + strconv.Itoa(angle) + "}"

		// send string over channel
		input_channel <- input
		<-input.ack
	}

	done <- true
}

func main() {
	// strEcho := "{\"distance\": 40, \"angle\": 90\"}"

	go connection_handler()
	//go receiver()
	go input()

	<-done
	<-done
}
