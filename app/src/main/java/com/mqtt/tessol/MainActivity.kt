package com.mqtt.tessol

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mqtt.tessol.ui.theme.MQTTSampleTheme
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage

class MainActivity : ComponentActivity() {

    lateinit var mqttClient: MqttAndroidClient
    val topic = "foo/bar"

    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MQTTSampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        Text(text = "MQTT Sample")
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { connectMqtt() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "Connect to MQTT")
                            }

                            Button(
                                onClick = { disconnectMqtt() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "Disconnect to MQTT")
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { publish("First message from app") },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "Publish message to MQTT")
                            }

                            Button(
                                onClick = { subscribe() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "Subscribe to MQTT")
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { unsubscribe() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "Unsubscribe from MQTT")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun connectMqtt() {
        val clientId = MqttClient.generateClientId()
        Log.d(TAG, clientId)
        try {
            mqttClient = MqttAndroidClient(applicationContext, "tcp://broker.hivemq.com:1883", clientId)
            val token = mqttClient.connect()
            token.actionCallback = object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d("MQTT", "connection success")
                    subscribe()
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d("MQTT", "connection failure")
                }
            }

            mqttClient.setCallback(object : MqttCallback {
                override fun connectionLost(cause: Throwable?) {
                    Log.d("MQTT", "connection lost")
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    Log.d(
                        "MQTT",
                        "message arrived on topic " + topic + "  message:" + message?.toString()
                    )
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    Log.d("MQTT", "deliveryComplete")
                }
            })
        }
//        catch (e: MqttException) {
//            e.printStackTrace()
//        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun subscribe() {
        val token = mqttClient.subscribe(topic, 1) ?: return
        token.actionCallback = object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                Log.d("MQTT", "subscribe success")
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Log.d("MQTT", "subscribe failed")
            }
        }
    }

    fun publish(payload: String) {
        val message = MqttMessage()
        message.payload = payload.toByteArray()
        message.qos = 1
        message.isRetained = false
        mqttClient.publish(topic, message, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                Log.d("MQTT", "publish success")
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Log.d("MQTT", "publish failed")
            }
        })
    }

    override fun onStop() {
        unsubscribe()
        disconnectMqtt()
        super.onStop()
    }

    private fun unsubscribe() {
        mqttClient.unsubscribe(topic)
    }

    private fun disconnectMqtt() {
        try {
            val token = mqttClient.disconnect() ?: return
            token.actionCallback = object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d("MQTT", "disconnect success")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d("MQTT", "disconnect failed");
                }
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
        catch (e: MqttException) {
            e.printStackTrace()
        }
    }
}
