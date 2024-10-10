import Error from "./error.png"
import Styles from "./errorIndicator.module.css"

export function ErrorIndicator() {
    return <div className={Styles.container}>
        <img src={Error} width={400} />
        Your token might have expired. You may try running /iom again.
    </div>
}