/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Utils;
import frc.robot.subsystems.SwerveDrivetrain;
import frc.robot.Utils;
import static frc.robot.Constants.*;

public class SwerveJoystickCommand extends CommandBase {
  private SwerveDrivetrain m_drivetrain;
  private XboxController m_controller;
  private Utils m_utils;

  /**
   * Creates a new SwerveJoystickCommand.
   */
  public SwerveJoystickCommand(SwerveDrivetrain drivetrain) {
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(drivetrain);
    m_drivetrain = drivetrain;
    m_controller = new XboxController(0);
    m_utils = new Utils();
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() { // if dont apply deadzone, then relation between joystick/speed is linear and no
                          // deadzones, we need these
    double x = m_utils.oddSquare(m_utils.deadZones(-m_controller.getY(Hand.kLeft), 0.2)) * MAX_METERS_PER_SECOND; // apply functions to controller values to 1) check deadzone 2) apply
                                                                                                                  // quadratic relation between controller/speed
    double y = m_utils.oddSquare(m_utils.deadZones(m_controller.getX(Hand.kLeft), 0.2)) * MAX_METERS_PER_SECOND;
    double rot = m_utils.oddSquare(m_utils.deadZones(m_controller.getX(Hand.kRight), 0.2)) * MAX_RADIANS_PER_SECOND;

    m_drivetrain.move(x, y, rot, m_controller.getBumper(Hand.kRight));
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    m_drivetrain.move(0, 0, 0, false);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
